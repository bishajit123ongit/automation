package com.automation.ui.methods;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static java.io.File.separator;

public class GenericMethods extends UtilityMethods {

    //Data read functionality from excel
    protected static boolean ExcelSheetDataFetch(String FileName, String FileLocation, String DataSheetName, String ExtraParam) {

        String currentMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        List<LinkedHashMap<String, String>> dataList = new ArrayList<>();
        HashMap<String, String> data = new HashMap<>();
        String excelFile;
        String excelFilePathCheck;
        PARSE = null;
        if (ExtraParam != null) {
            if (!ExtraParam.equals("NULL")) {
                splitedExtraParam = ExtraParam.replace("\\:", "&quot;").split("\\|");
                for (String s : splitedExtraParam) {
                    splitedValue = s.split(":");
                    if (splitedValue.length > 1) {
                        splitedValue[1] = splitedValue[1].replace("&quot;", ":");
                    }
                    switch (splitedValue[0]) {
                        case "PARSE":
                            PARSE = splitedValue[1];
                            break;
                    }
                }
            }
        }

        try {
            if (currentMethodName.equals("ExcelSheetDataReload") || FrmSubCheckDataSheetLoad(DataSheetName, null)) {
                if (FileName.contains("\"")) {
                    FileName = FileName.replace("\"", "");
                }
                if (FileLocation == null) {
                    excelFilePathCheck = currentGlobalParams.getProperty("GblTestRunListFolderPath").split("~")[0];
                    if (excelFilePathCheck.contains("\"")) {
                        excelFilePathCheck = excelFilePathCheck.replace("\"", "");
                    }
                    if (!excelFilePathCheck.endsWith(separator)) {
                        excelFilePathCheck = excelFilePathCheck + separator;
                    }
                    excelFile = excelFilePathCheck + FileName;
                } else if (FileLocation.toUpperCase().equals("NULL") || FileLocation.toUpperCase().equals("DEFAULT")) {
                    excelFilePathCheck = currentGlobalParams.getProperty("GblTestRunListFolderPath").split("~")[0];
                    if (excelFilePathCheck.contains("\"")) {
                        excelFilePathCheck = excelFilePathCheck.replace("\"", "");
                    }
                    if (!excelFilePathCheck.endsWith(separator)) {
                        excelFilePathCheck = excelFilePathCheck + separator;
                    }
                    excelFile = excelFilePathCheck + FileName;
                } else {
                    if (FileLocation.contains("\"")) {
                        FileLocation = FileLocation.replace("\"", "");
                    }
                    if (!FileLocation.endsWith(separator)) {
                        FileLocation = FileLocation + separator;
                    }
                    excelFile = FileLocation + FileName;
                }
                if (!excelFile.contains(".xlsx")) {
                    excelFile = excelFile + ".xlsx";
                }

                workbook = new XSSFWorkbook(excelFile);
                DataSheetName = DataSheetName.trim();
                if (workbook.getSheet(DataSheetName) != null) {
                    sheet = workbook.getSheet(DataSheetName);
                } else {
                    if (DataSheetName.equalsIgnoreCase("ArrayParams")) {
                        return false;
                    } else {
                        message = "DataSheet is not found, please check sheet name!";
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                        workbook.close();
                        return false;
                    }
                }
                int rowCount = sheet.getLastRowNum();
                if (DataSheetName.trim().equalsIgnoreCase("TestParams") || DataSheetName.trim().equalsIgnoreCase("BatchTestRunParams")) {
                    for (int i = 0; i <= rowCount; i++) {
                        if (FrmSubEmptyCellCheck(sheet, i, 0) && FrmSubEmptyCellCheck(sheet, i, 1)) {
                            Row row = sheet.getRow(i);
                            String key = row.getCell(0).getStringCellValue().trim();
                            String value = row.getCell(1).getStringCellValue();
                            data.put(key, value);
                            if ((key.trim().startsWith("##") && key.trim().endsWith("EOF")) ||
                                    (value.trim().startsWith("##") && value.trim().endsWith("EOF"))) {
                                break;
                            }
                        }
                    }
                    array.put(DataSheetName, data);

                    if (PARSE != null) {
                        if (PARSE.trim().equalsIgnoreCase("YES") || PARSE.trim().equalsIgnoreCase("TRUE")) {
                            if (array.get(DataSheetName) != null) {
                                if (array.get(DataSheetName).size() > 0) {
                                    for (Object paramKey : array.get(DataSheetName).keySet()) {
                                        array.get(DataSheetName).replace(paramKey.toString(), FrmSubCellParamParse(array.get(DataSheetName).get(paramKey), null));
                                    }
                                }
                            }
                            for (Object paramKey : array.get(DataSheetName).keySet()) {
                                String Param = array.get(DataSheetName).get(paramKey);
                                if (Param != null) {
                                    Param = FrmParamParse(Param, ExtraParam);
                                    if (paramKey.toString().trim().startsWith("##")) {
                                        continue;
                                    }
                                    if (paramKey.toString().trim().startsWith("Gbl")) {
                                        if (currentGlobalParams.containsKey(paramKey.toString().trim())) {
                                            currentGlobalParams.replace(paramKey.toString().trim(), Param);
                                        } else {
                                            currentGlobalParams.put(paramKey.toString().trim(), Param);
                                        }
                                    } else if (paramKey.toString().trim().startsWith("Cov")) {
                                        if (array.get("CarryOverParams") == null) {
                                            array.put("CarryOverParams", carryOverParamArray);
                                        }
                                        if (array.get("CarryOverParams").containsKey(paramKey.toString().trim())) {
                                            array.get("CarryOverParams").replace(paramKey.toString().trim(), Param);
                                        } else {
                                            array.get("CarryOverParams").put(paramKey.toString().trim(), Param);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (sheet.getRow(0).getLastCellNum() > 0) {
                    String value;
                    for (int i = 1; i <= rowCount; i++) {
                        Row row = sheet.getRow(0);
                        LinkedHashMap<String, String> rowData = new LinkedHashMap<>();
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            if (FrmSubEmptyCellCheck(sheet, i, j)) {
                                String key = row.getCell(j).getStringCellValue().trim();
                                if (PARSE != null) {
                                    if (PARSE.trim().equalsIgnoreCase("YES") || PARSE.trim().equalsIgnoreCase("TRUE")) {
                                        value = FrmSubCellParamParse(sheet.getRow(i).getCell(j).getStringCellValue(), null);
                                    } else {
                                        value = sheet.getRow(i).getCell(j).getStringCellValue();
                                    }
                                } else {
                                    value = sheet.getRow(i).getCell(j).getStringCellValue();
                                }
                                rowData.put(key, value);
                                if ((key.trim().startsWith("##") && key.trim().endsWith("EOF")) ||
                                        (value.trim().startsWith("##") && value.trim().endsWith("EOF"))) {
                                    break;
                                }
                            }
                        }
                        dataList.add(rowData);
                    }
                    arrayList.put(DataSheetName, dataList);
                }
            }
            return true;
        } catch (IOException e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean BrowserWindowClose(String BrowserWindowName, String ExtraParam) {
        try {
            if (BrowserWindowName.equals("DEFAULT")) {
                if (driver == null) {
                    return true;
                }
                driver.quit();
                driver = null;
            } else {
                message = "BrowserWindowName parameter value should be DEFAULT!";
                FrmSubErrorMsgArrayInsert(message, null);
                message = "";
                return false;
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    //Generic Functionality for test report writing
    public static boolean TextOutputLineWrite(String TheOutputLine, String TextToAppend, String FileName, String FileNameSuffix, String FileLocation, String ExtraParam) {

        try {
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            boolean replace = false;

            if (ExtraParam != null) {
                if (!ExtraParam.equals("NULL")) {
                    splitedExtraParam = ExtraParam.replace("\\:", "&quot;").split("\\|");
                    for (String s : splitedExtraParam) {
                        splitedValue = s.split(":");
                        if (splitedValue.length > 1) {
                            splitedValue[1] = splitedValue[1].replace("&quot;", ":");
                        }
                        switch (splitedValue[0].trim()) {
                            case "REPLACE":
                                if (splitedValue[1].trim().equalsIgnoreCase("Yes") || splitedValue[1].trim().equalsIgnoreCase("True")) {
                                    replace = true;
                                }
                                break;
                        }
                    }
                }
            }

            if (FileNameSuffix != null) {
                if (FileName.contains("\"")) {
                    FileName = FileName.replace("\"", "");
                }
                if (FileNameSuffix.equals("NULL")) {
                    FileName = FileName;
                } else if (FileNameSuffix.equals("DEFAULT")) {
                    FileName = FileName + FrmSubDateTimeStringFormat("DATETIMESTAMP", null, null, null, null);
                } else {
                    FileName = FileName + FileNameSuffix;
                }
            }
            if (TheOutputLine.equals("TESTREPORTARRAY") || TheOutputLine.equals("BATCHREPORTARRAY")) {
                if (TheOutputLine.equals("TESTREPORTARRAY")) {
                    reportcount++;
                } else {
                    bacthcount++;
                }
                if (!FileName.endsWith(".txt") && !FileName.endsWith(".html")) {
                    FileName = FileName + ".txt";
                }
            } else if (TheOutputLine.equals("TESTLOGARRAY")) {
                logcount++;
                if (!FileName.endsWith(".log")) {
                    FileName = FileName + ".log";
                }
            }

            if (FileLocation.contains("\"")) {
                FileLocation = FileLocation.replace("\"", "");
            }
            if (!FileLocation.endsWith(separator)) {
                FileLocation = FileLocation + separator;
            }
            File file = new File(FileLocation + FileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                if ((logcount == 1 && TheOutputLine.equals("TESTLOGARRAY")) ||
                        (reportcount == 1 && TheOutputLine.equals("TESTREPORTARRAY")) ||
                        (bacthcount == 1 && TheOutputLine.equals("BATCHREPORTARRAY"))
                ) {
                    file.delete();
                    file.createNewFile();
                } else {
                    if (replace) {
                        file.delete();
                        file.createNewFile();
                    }
                }
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            StringBuilder stringBuilder = new StringBuilder();

            if (TheOutputLine.equals("TESTREPORTARRAY")) {
                for (String value : TestResultReportArray) {
                    stringBuilder.append(value);
                }
                TheOutputLine = stringBuilder.toString();
            } else if (TheOutputLine.equals("TESTLOGARRAY")) {
                for (String value : TestDetailLogArray) {
                    stringBuilder.append(value);
                }
                TheOutputLine = stringBuilder.toString();
            } else if (TheOutputLine.equals("BATCHREPORTARRAY")) {
                for (String value : BatchRunReportArray) {
                    stringBuilder.append(value);
                }
                TheOutputLine = stringBuilder.toString();
            } else {
                if (!methodName.equalsIgnoreCase("RestApiGroupRequestResponseValidate")
                        && !methodName.equalsIgnoreCase("ObjectGroupActionVerify")
                        && !methodName.equalsIgnoreCase("ObjectGroupActionSet")) {
                    if (TextToAppend == null) {
                        TextToAppend = "";
                    } else {
                        if (TextToAppend.trim().equals("NULL")) {
                            TextToAppend = "";
                        } else {
                            TextToAppend = " " + TextToAppend;
                        }
                    }
                    message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                            + " " + FrmSubRightPad("Report Message", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + RowNumber + "TextOutputLineWrite::\t" + TheOutputLine + TextToAppend + ".\n";
                    FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                    message = "";
                }
                if (TestDetailLogArray.size() > 0) {
                    TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);
                    FrmSubDataArrayClear("TestDetailLogArray", null);
                }
                if (TestResultReportArray.size() > 0) {
                    TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                    FrmSubDataArrayClear("TestResultReportArray", null);
                }
            }

            if (FileName.endsWith(".html")) {
                TheOutputLine = "<pre>" + TheOutputLine + "</pre>";
            }
            if (TextToAppend != null) {
                if (FileName.endsWith(".html")) {
                    if (!TextToAppend.trim().equals("PASS") && !TextToAppend.trim().equals("FAIL")) {
                        TextToAppend = "<pre>" + TextToAppend + "</pre>";
                    }
                }
                if (!TextToAppend.equals("NULL")) {
                    if (!TextToAppend.endsWith("\n")) {
                        bufferedWriter.write(TheOutputLine + " " + TextToAppend + "\n");
                    } else {
                        bufferedWriter.write(TheOutputLine + " " + TextToAppend);
                    }
                } else {
                    if (!TheOutputLine.endsWith("\n")) {
                        TheOutputLine = TheOutputLine + "\n";
                    }
                    bufferedWriter.write(TheOutputLine);
                }
            } else {
                if (!TheOutputLine.endsWith("\n")) {
                    TheOutputLine = TheOutputLine + "\n";
                }
                bufferedWriter.write(TheOutputLine);
            }
            //bufferedWriter.newLine();
            bufferedWriter.close();
            return true;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean ScreenShotCapture(String ScreenShotType, String ImageType, String FileName, String FileNameSuffix, String FileLocation, String ExtraParam) {

        try {
            if (FileName.equals("DEFAULT")) {
                FileName = currentGlobalParams.getProperty("GblScreenShotFileName").split("~")[0] + String.format("%02d", screenShotCount);
                screenShotCount++;
            }
            if (FileNameSuffix != null) {
                if (!FileNameSuffix.equals("NULL") && !FileNameSuffix.equals("DEFAULT")) {
                    FileName = FileName + FileNameSuffix;
                } else if (FileNameSuffix.equals("DEFAULT")) {
                    FileName = FileName + FrmSubDateTimeStringFormat("DATETIMESTAMP", null, null, null, null);
                }
            }

            TakesScreenshot screenshot = ((TakesScreenshot) driver);
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile, directory;

            switch (ImageType.toUpperCase()) {
                case "DEFAULT":
                case "JPG":
                case "JPEG":
                    ImageType = ".jpg";
                    break;
                case "PNG":
                    ImageType = ".png";
                    break;
                case "BMP":
                    ImageType = ".bmp";
                    break;
                case "TIF":
                    ImageType = ".tiff";
                    break;
                case "PDF":
                    ImageType = ".pdf";
                    break;
            }
            if (FileLocation != null) {
                if (FileLocation.equals("DEFAULT")) {
                    FileLocation = currentGlobalParams.getProperty("GblScrnShotFolderPath").split("~")[0];
                    if (FileLocation.contains("\"")) {
                        FileLocation = FileLocation.replace("\"", "");
                    }
                    if (!FileLocation.endsWith(separator)) {
                        FileLocation = FileLocation + separator;
                    }
                    destinationFile = new File(FileLocation + FileName + ImageType);
                } else {
                    if (FileLocation.contains("\"")) {
                        FileLocation = FileLocation.replace("\"", "");
                    }
                    if (!FileLocation.endsWith(separator)) {
                        FileLocation = FileLocation + separator;
                    }
                    destinationFile = new File(FileLocation + FileName + ImageType);
                }
                directory = new File(FileLocation);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                FileUtils.copyFile(sourceFile, destinationFile);
            }
            return true;

        } catch (NoSuchWindowException ex) {
            try {
                message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                        + " " + FrmSubRightPad("ScreenShotCapture", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + "No UI focused to take screenshot!" + "\n";
                FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                message = "";
                return false;
            } catch (Exception e) {
                FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
                return false;
            }
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean BrowserWindowMaximize(String BrowserWindowName, String ExtraParam) {
        try {
            if (ExtraParam == null) {
                ExtraParam = "NULL";
            }
            if (BrowserWindowName.equals("NULL") || BrowserWindowName.equals("DEFAULT")) {
                if (ExtraParam.equals("FULLSCREEN")) {
                    driver.manage().window().fullscreen();
                } else {
                    driver.manage().window().maximize();
                }
            } else {
                message = "BrowserWindowName parameter value should be DEFAULT or NULL!";
                FrmSubErrorMsgArrayInsert(message, null);
                message = "";
                return false;
            }

            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean ObjectVisibilityAssert(String ObjectName, String MaxTimeWait, String ExtraParam) {
        try {
            int GblMaxTimeWait = Integer.parseInt(currentGlobalParams.getProperty("GblMaxWaitTime").split("~")[0]);
            if (ObjectName.contains("Gui")) {
                objectProperties = FrmSubActiveObjectRepositoryGet(ObjectName);
            }
            if (MaxTimeWait.equals("DEFAULT")) {
                final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(GblMaxTimeWait));
                wait.until(ExpectedConditions.visibilityOfElementLocated(operation.getObject(objectProperties[1], objectProperties[0])));
            } else if (MaxTimeWait.equals("NULL")) {
                final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(0));
                wait.until(ExpectedConditions.visibilityOf(driver.findElement(operation.getObject(objectProperties[1], objectProperties[0]))));
            } else {
                final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(MaxTimeWait)));
                wait.until(ExpectedConditions.visibilityOfElementLocated(operation.getObject(objectProperties[1], objectProperties[0])));
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean WindowsDocumentAttach(String DocFileName, String DocFilePath, String ExtraParam) {
        try {
            result = false;
            clipboardText = "";

            InputStream stream = GenericMethods.class.getResourceAsStream("/utilities/UploadFiles.exe");
            File exeTempFile = File.createTempFile("UploadFiles", ".exe");
            exeTempFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(stream, exeTempFile);
            stream.close();

            createdFullFilePath = FrmSubFilePathCreate(DocFileName, DocFilePath);

            if (!createdFullFilePath.equalsIgnoreCase("NULL")) {
                String fullPath = exeTempFile.toString() + " " + createdFullFilePath;
                FrmSubUploadFileExeRun(fullPath);
            } else {
                if (ExtraParam != null) {
                    switch (ExtraParam.toUpperCase()) {
                        case "CANCEL":
                            FrmSubUploadFileExeRun(exeTempFile.toString() + " " + "CANCEL");
                            break;
                        case "CLOSE":
                        case "CLEANUP":
                            FrmSubUploadFileExeRun(exeTempFile.toString() + " " + "CLOSE");
                            break;
                        case "SCRNSHOT":
                            ScreenShotCapture("DEAFAULT", "DEFAULT", "FileUploadPage", "DEFAULT", "DEFAULT", "NULL");
                            break;
                        default:
                            message = "Extra parameter value is incorrect within WindowsDocumentAttach method.";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;
                    }
                } else {
                    message = "Extra parameter value is null within WindowsDocumentAttach method.";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
                }
            }

            UtlSyncTimeWait("1", null);
            start = System.currentTimeMillis();
            end = start + 60000;
            while (true) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                hasStringText = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
                if (hasStringText) {
                    try {
                        clipboardText = clipboard.getData(DataFlavor.stringFlavor).toString();
                    } catch (UnsupportedFlavorException | IOException ex) {
                        FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(ex), null);
                    }
                    if (clipboardText.startsWith("PASS")) {
                        result = true;
                    } else if (clipboardText.startsWith("FAIL")) {
                        result = false;
                        ScreenShotCapture("DEAFAULT", "DEFAULT", "FileUpFailed", "DEFAULT", "DEFAULT", "NULL");
                        FrmSubUploadFileExeRun(UploadFileExePath + " " + "CLOSE");
                        message = "File upload failed." + clipboardText;
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                    }
                    break;
                }
                UtlSyncTimeWait("1", null);
                if (System.currentTimeMillis() > end) {
                    break;
                }
            }
            return result;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static void FrmSubUploadFileExeRun(String fullFilePath) {
        try {
            Runtime.getRuntime().exec(fullFilePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean ObjectActionSet(String ObjectName, String ActionType, String ValueToBeSet, String ExtraParam) {
        try {
            String currentMethodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            FOUNDOBJECT = "";

            if (ObjectName.startsWith("Gui")) {
                ObjectName = ObjectName.trim();
                objectProperties = FrmSubActiveObjectRepositoryGet(ObjectName);
            }
            if(ObjectName.equalsIgnoreCase("LISTBOX")){
                ObjectName = "GuiListBox1";
                objectProperties = FrmSubActiveObjectRepositoryGet(ObjectName);
            }

            if(ObjectName.split("~").length>1){
                FOUNDOBJECT = ObjectName.split("~")[2].trim();
                webElement = driver.findElement(By.xpath((ObjectName.split("~")[1])));
                FrmSubScrollToElement(driver,webElement);
                result = FrmSubObjectActionSet(ObjectName, ActionType, ValueToBeSet, ExtraParam);
            }

            if (objectProperties != null) {
                FOUNDOBJECT = objectProperties[2].trim();
                webElement = driver.findElement(By.xpath(objectProperties[1]));
                FrmSubScrollToElement(driver,webElement);
                result = FrmSubObjectActionSet(ObjectName, ActionType, ValueToBeSet, ExtraParam);
            }


            return true;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean WebTableCellActionSet(String DataName, String ColIndex,String RowIndex,String ObjectType,String ActionType,String ValueToBeSet, String ExtraParam){
        try{
            String delete="", edit="";
            if(ExtraParam!=null){
                splitedExtraParam = ExtraParam.split("\\|");
                for(String s : splitedExtraParam){
                    splitedValue = s.split(":");
                    if(splitedValue[0].equals("ACTIONTYPE")){
                        switch (splitedValue[1]){
                            case "DELETE":
                                delete = splitedValue[1];
                                break;
                            case "EDIT":
                                edit = splitedValue[1];
                                break;
                        }
                    }
                }
            }
            if(DataName.trim()!=null){
                partialXpath = utilityObjects.getProperty("GuiTableRow").split("~");
                if(FrmSubIsElementPresent(By.xpath(partialXpath[1]+DataName+partialXpath[2]))){
                    xpath = partialXpath[1]+DataName+partialXpath[2];
                }
                if(edit!=null && !edit.isEmpty()){
                    xpath = xpath + "//i[contains(@class,'bi-pencil-fill')]//parent::button";
                }
                if(delete!=null && !delete.isEmpty()){
                    xpath = xpath + "//i[contains(@class,'bi-trash')]//parent::button";
                }
                if(xpath.equals("")){
                    message = "XPATH Is null";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
                }
                else{
                    generateXpath = "XPATH~"+xpath+"~DATACELL";
                    ObjectActionSet(generateXpath,ActionType,ValueToBeSet,ExtraParam);

                }

            }
            return true;
        }
        catch(Exception e){
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e),null);
            return false;
        }
    }

    public static boolean BrowserTabClose(String TabObjectName, String InstanceNum, String ExtraParam) {
        try {
            String[] tabObjParts;
            String tabObjxpath = "";
            boolean xpathIdentifier = false;
            List<Integer> listOfFoundTabs = new ArrayList<>();
            if (InstanceNum != null) {
                if (InstanceNum.equalsIgnoreCase("DEFAULT") || InstanceNum.equalsIgnoreCase("FIRST") || InstanceNum.equalsIgnoreCase("NULL")) {
                    InstanceNum = "1";
                }
            } else {
                InstanceNum = "1";
            }

            UtlSyncTimeWait("1", null);
            if (driver != null) {
                tabs = new ArrayList<>(driver.getWindowHandles());
            }

            if (TabObjectName.equals("ALL")) {
                for (int i = 0; i < tabs.size(); i++) {
                    driver.switchTo().window(tabs.get(i)).close();
                }
            } else if (TabObjectName.toUpperCase().equals("DEFAULT")) {
                if (tabs.size() > 1) {
                    for (int i = 0; i < tabs.size(); i++) {
                        if (driver.switchTo().window(tabs.get(i)).getTitle().equalsIgnoreCase(commonObjects.getProperty("GuiAppHomePageTab").split("~")[1])) {
                            for (int j = i + 1; j < tabs.size(); j++) {
                                driver.switchTo().window(tabs.get(j)).close();
                            }
                            driver.switchTo().window(tabs.get(i));
                            break;
                        }
                    }
                } else if (tabs.size() == 1) {
                    driver.switchTo().window(tabs.get(0));
                } else {
                    message = "There are no Tabs found!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
                }
            } else {
                tabObjParts = commonObjects.getProperty(TabObjectName).split("~");
                if (!tabObjParts[2].trim().equals("BROWSERTAB")) {
                    message = "Given object is not a BROWSERTAB!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
                }
                if (tabObjParts[0].trim().equals("NAME")) {
                    TabName = tabObjParts[1];
                } else if (tabObjParts[0].trim().equals("XPATH")) {
                    tabObjxpath = tabObjParts[1];
                    xpathIdentifier = true;
                }

                for (int i = 0; i < tabs.size(); i++) {
                    if (xpathIdentifier) {
                        driver.switchTo().window(tabs.get(i));
                        if (FrmSubIsElementPresent(By.xpath(tabObjxpath))) {
                            listOfFoundTabs.add(i);
                        }
                    } else {
                        if (driver.switchTo().window(tabs.get(i)).getTitle().equalsIgnoreCase(TabName)) {
                            listOfFoundTabs.add(i);
                        }
                    }
                }
                if (listOfFoundTabs.size() >= 1) {
                    if (InstanceNum.trim().equals("ALL")) {
                        for (int tabPos : listOfFoundTabs) {
                            driver.switchTo().window(tabs.get(tabPos)).close();
                        }
                    } else if (InstanceNum.trim().equals("LAST")) {
                        driver.switchTo().window(tabs.get(listOfFoundTabs.get(listOfFoundTabs.size() - 1))).close();
                    } else if (listOfFoundTabs.size() >= Integer.parseInt(InstanceNum)) {
                        driver.switchTo().window(tabs.get(listOfFoundTabs.get(Integer.parseInt(InstanceNum) - 1))).close();
                    } else {
                        message = "Instance no is wrong for closing tab!";
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                        return false;
                    }
                } else {
                    message = "Tab is not found to close!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

}
