package com.automation.ui.methods;

import com.automation.ui.selenium.SeleniumLocators;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separator;

public class FrameworkSubRoutine {
    public static WebDriver driver;
    public static Actions action;
    public static Properties currentGlobalParams, adminObjects, commonObjects, utilityObjects;
    public static String[] partialXpath, objectProperties, objRepToLoad, splitedExtraParam, splitedValue;
    public static String FullFilePath,createdFullFilePath,clipboardText,UploadFileExePath,generateXpath,xpath, TabName, AUXKEY, FOUNDOBJECT, LoginPageTabName, HomePageTabName, MiscComments, TestReportFlag, JAR_FILE_PATH, testerName, screenShotFolderPath, testTitleName, TestDataFileName, TestDataFilePath, output, RowMarkingFlag, parameterToBeReplaced, PARSE, startBatchTime, testReportFile, testReportFolderPath, testLogFolderPath, testLogFile, testsFolderPath, testsToRunListFile, batchReportFolderPath, batchTestRunReportFile, message, RowNumber;
    public static XSSFWorkbook workbook;
    public static XSSFSheet sheet;
    public static Map<String, String> tabsArray = new HashMap<>();
    public static Table<String, Integer, String> tabsTable = HashBasedTable.create();
    public static HashMap<String, String> data = new HashMap<String, String>();
    public static Map<String, String> carryOverParamArray = new HashMap<>();
    public static Map<String, Map<String, String>> array = new HashMap<String, Map<String, String>>();
    public static Map<String, List<LinkedHashMap<String, String>>> arrayList = new HashMap<>();
    public static Map<String, List<String>> rowToSkipArray = new HashMap<>(), arrayOfDataSheet = new LinkedHashMap<>();
    public static ArrayList<String> tabs, BatchRunReportArray, TestResultReportArray, TestDetailLogArray, rowToSkipSheetName = new ArrayList<>();
    public static int screenShotCount, testDataIndex, serialNo, reportcount = 0, bacthcount = 0, logcount = 0;
    public static Object[] parameters, delimitedParameters;
    public static boolean hasStringText,result, GblPrevMethodReturnCode, GblLastMethodReturnCode, BROWSERHEADLESS;
    public static SeleniumLocators operation = new SeleniumLocators(driver);
    public static WebElement webElement;
    public static long start,end;

    static {
        try {
            JAR_FILE_PATH = new File("").getAbsolutePath();
            BatchRunReportArray = new ArrayList<>();
            TestResultReportArray = new ArrayList<>();
            TestDetailLogArray = new ArrayList<>();
            currentGlobalParams = FrmSubObjectRepositoryGet("GlobalParams");
            commonObjects = FrmSubObjectRepositoryGet("CommonObjects");
            utilityObjects = FrmSubObjectRepositoryGet("UtilityObjects");
            LoginPageTabName = commonObjects.getProperty("GuiLoginTab").split("~")[1];
        } catch (Exception e) {

        }
    }

    public static String FrmSubFilePathCreate(String DocFileName, String DocFilePath) {
        FullFilePath = "";
        DocFilePath = DocFilePath.replace("\"", "");
        switch (DocFilePath.toUpperCase()) {
            case "DEFAULT":
                switch (DocFileName.toUpperCase()) {
                    case "NULL":
                    case "DEFAULT":
                        if (!currentGlobalParams.getProperty("GblDocFilePath").split("~")[0].endsWith(separator)) {
                            FullFilePath = currentGlobalParams.getProperty("GblDocFilePath").split("~")[0] + separator;
                        } else {
                            FullFilePath = currentGlobalParams.getProperty("GblDocFilePath").split("~")[0];
                        }
                        break;
                    default:
                        if (!DocFileName.endsWith(separator) && !currentGlobalParams.getProperty("GblDocFilePath").split("~")[0].endsWith(separator)) {
                            FullFilePath = currentGlobalParams.getProperty("GblDocFilePath").split("~")[0] + separator + DocFileName;
                        } else {
                            FullFilePath = currentGlobalParams.getProperty("GblDocFilePath").split("~")[0] + DocFileName;
                        }
                }
                break;
            case "NULL":
                switch (DocFileName.toUpperCase()) {
                    case "NULL":
                    case "DEFAULT":
                        FullFilePath = "NULL";
                        break;
                    default:
                        if (!DocFileName.endsWith(separator)) {
                            FullFilePath = separator + DocFileName;
                        } else {
                            FullFilePath = DocFileName;
                        }
                }
                break;
            default:
                switch (DocFileName.toUpperCase()) {
                    case "NULL":
                    case "DEFAULT":
                        if (!DocFilePath.endsWith(separator)) {
                            FullFilePath = DocFilePath + separator;
                        } else {
                            FullFilePath = DocFilePath;
                        }
                        break;
                    default:
                        if (!DocFileName.endsWith(separator) && !DocFilePath.endsWith(separator)) {
                            FullFilePath = DocFilePath + separator + DocFileName;
                        } else {
                            FullFilePath = DocFilePath + DocFileName;
                        }
                }
        }

        return FullFilePath;
    }


    // Module wise specific object repository load functionality
    public static boolean FrmSubRepositoryLoad() {
        objRepToLoad = currentGlobalParams.getProperty("GblObjRepPrpPreLoad").split("~")[0].split(",");
        try {
            for (Object object : objRepToLoad) {
                switch ((object.toString())) {
                    case "Admin":
                        adminObjects = FrmSubObjectRepositoryGet("AdminObjects");
                        break;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static String[] FrmSubActiveObjectRepositoryGet(String ObjectName) throws Exception {
        try {
            switch (currentGlobalParams.getProperty("GblActiveModule").split("~")[0]) {
                case "ADMIN":
                    FrmSubObjectFind(adminObjects, "AdminObjects", ObjectName);
                    break;
            }
            return objectProperties;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            throw new Exception(e);
        }
    }

    public static boolean FrmSubObjectFind(Properties properties, String propertyFileName, String ObjectName) {
        try {
            if (properties.isEmpty()) {
                properties = FrmSubObjectRepositoryGet(propertyFileName);
            }
            if (properties.getProperty(ObjectName) != null) {
                objectProperties = properties.getProperty(ObjectName).split("~");
                return true;
            } else if (commonObjects.getProperty(ObjectName) != null) {
                objectProperties = commonObjects.getProperty(ObjectName).split("~");
                return true;
            } else {
                System.out.println("Incorrect object name : " + ObjectName);
                message = "Object not found! Incorrect object name : " + ObjectName;
                FrmSubErrorMsgArrayInsert(message, null);
                message = "";
                return false;
            }
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    //Object repository property file load functionality
    public static Properties FrmSubObjectRepositoryGet(String propertyFileName) {
        try {
            InputStream stream = null;
            stream = ClassLoader.getSystemResourceAsStream(propertyFileName + ".properties");
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (Exception e) {
            return null;
        }
    }

    //excel null value check
    public static boolean FrmSubEmptyCellCheck(XSSFSheet sheet, int row, int col) {
        try {
            return sheet.getRow(row).getCell(col) != null
                    && sheet.getRow(row).getCell(col).getStringCellValue() != null
                    && !sheet.getRow(row).getCell(col).getStringCellValue().isEmpty();
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }

    public static boolean FrmSubTestHeaderFooterSet(String HeaderFooterFlag, String ExtraParam) {
        try {
            if (HeaderFooterFlag.equalsIgnoreCase("BATCHHEADER")) {
                BatchRunReportArray.add(" " + StringUtils.repeat("_", 118) + " \n");
                BatchRunReportArray.add("|" + FrmSubRightPad("", 118) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tBatch Run Date", 30) + ":\t" + FrmSubRightPad(FrmSubDateTimeStringFormat("EEEE, MMMM dd, yyyy", null, null, null, null), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tBatch Run Start Time", 30) + ":\t" + FrmSubRightPad(FrmSubDateTimeStringFormat("hh:mm:ss:SSS a", null, null, "outBatchStartTime", null), 75) + "|\n");
                startBatchTime = FrmSubDynamicArrayValueGet("TestParams", "outBatchStartTime");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tAUT Name", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("AutName"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tAUT Version", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("AutVersion"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tWindows OS Name", 30) + ":\t" + FrmSubRightPad(System.getProperties().getProperty("os.name"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tWindows OS Version", 30) + ":\t" + FrmSubRightPad(System.getProperties().getProperty("os.version"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tWeb Browser Name", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("WebBrowserName"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tWeb Browser Version", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("WebBrowserVersion"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tTest Tool Name", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("TestToolName"), 75) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tTest Tool Version", 30) + ":\t" + FrmSubRightPad(array.get("BatchTestRunParams").get("TestToolVersion"), 75) + "|\n");
                BatchRunReportArray.add("|" + StringUtils.repeat("_", 118) + "|\n");
                BatchRunReportArray.add("\n\n");
                BatchRunReportArray.add(" " + StringUtils.repeat("_", 118) + " \n");
                BatchRunReportArray.add("|" + FrmSubRightPad("", 118) + "|\n");
                BatchRunReportArray.add("|" + FrmSubRightPad("\t\t\tTest No.\t", 15) + FrmSubRightPad("Test Name", 50) + FrmSubRightPad("Tester Name", 20) + FrmSubRightPad("Tester Run Status", 22) + "|\n");
                BatchRunReportArray.add("|" + StringUtils.repeat("_", 118) + "|\n");
                BatchRunReportArray.add("\n");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean FrmSubFileDirectoryGeneration(String FileLocation, String FileName) {
        try {
            File directory, file;
            if (FileLocation.contains("\"")) {
                FileLocation = FileLocation.replace("\"", "");
            }
            if (!FileLocation.endsWith("\\\\") && !FileLocation.endsWith("\\")) {
                FileLocation = FileLocation + "\\\\";
            }
            directory = new File(FileLocation);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            file = new File(FileLocation + FileName);
            if (file.exists()) {
                file.delete();
            } else {
                file = new File(FileLocation + FileName + ".log");
                if (file.exists()) {
                    file.delete();
                } else {
                    file = new File(FileLocation + FileName + ".txt");
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String FrmSubDateTimeStringFormat(String FormatOutputMask, String FormatInputMask, String InputDateTimeString, String OutDateTimeString, String ExtraParam) {
        try {
            LocalDateTime dateTimeObject = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter;
            String dateTime = null;

            if (FormatOutputMask.equals("DATETIMESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblDateTimeFormat").split("~")[0]);
            } else if (FormatOutputMask.equals("TIMESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblTimeFormat").split("~")[0]);
            } else if (FormatOutputMask.equals("DATESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblDateFormat").split("~")[0]);
            } else {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FormatOutputMask);
            }

            dateTime = dateTimeObject.format(dateTimeFormatter);

            if (OutDateTimeString != null && dateTime != null) {
                if (array.get("TestParams") == null) {
                    array.put("TestParams", new HashMap<>());
                }
                if (array.get("TestParams").containsKey(OutDateTimeString)) {
                    array.get("TestParams").replace(OutDateTimeString, dateTime);
                } else {
                    array.get("TestParams").put(OutDateTimeString, dateTime);
                }
            }

            return dateTime;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "FAIL";
        }
    }

    public static String FrmSubErrorStackTracePrint(Exception exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static boolean FrmSubErrorMsgArrayInsert(String ErrorMessage, String ExtraParam) {
        try {
            if (ErrorMessage.equals("DEFAULT")) {
                message = "Error Message:\t" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":: " + currentGlobalParams.getProperty("GblFrmErrorMessage").split("~")[0] + ".\n";
            } else if (Thread.currentThread().getStackTrace()[2].getMethodName().equalsIgnoreCase("UtlProcessCpuUsageStabilitySynchronize")) {
                message = "CPU Usage Message:\t" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":: " + ErrorMessage + ".\n";
            } else {
                if (Thread.currentThread().getStackTrace()[2].getMethodName().equals("main")) {
                    message = "Error Message:\t" + "TestEngine" + ":: " + ErrorMessage + ".\n";
                } else {
                    message = "Error Message:\t" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":: " + ErrorMessage + ".\n";
                }
            }

            TestDetailLogArray.add(FrmSubDateTimeStringFormat("TIMESTAMP", null, null, null, null) + " " + message);

            if (message.toLowerCase().contains("com.mislbd.ababilng.testautomation") || message.toLowerCase().contains("org.openqa.selenium.support")) {
                currentGlobalParams.setProperty("GblSystemSpecificError", "TRUE");
                message = "Error Message:\t" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":: " + "Object specific system error occurred! \n";
            } else {
                currentGlobalParams.setProperty("GblSystemSpecificError", "FALSE");
            }
            TestResultReportArray.add(currentGlobalParams.get("GblCurrentTestStepTag") + ": " + message);
            message = "";
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean FrmSubTextMsgArrayInsert(String ArrayName, String TextMessage, String ExtraParam) {
        try {
            if (ArrayName.equals("TestResultReportArray") || ArrayName.equals("TESTREPORTARRAY")) {
                TestResultReportArray.add(TextMessage);
            } else if (ArrayName.equals("TestDetailLogArray") || ArrayName.equals("TESTLOGARRAY")) {
                TestDetailLogArray.add(TextMessage);
            } else if (ArrayName.equals("BatchRunReportArray") || ArrayName.equals("BATCHREPORTARRAY")) {
                BatchRunReportArray.add(TextMessage);
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean FrmSubDataArrayClear(String ArrayNameToEmpty, String ExtraParam) {
        try {
            if (ArrayNameToEmpty.equals("TestResultReportArray") || ArrayNameToEmpty.equals("TESTREPORTARRAY")) {
                TestResultReportArray.clear();
            } else if (ArrayNameToEmpty.equals("TestDetailLogArray") || ArrayNameToEmpty.equals("TESTLOGARRAY")) {
                TestDetailLogArray.clear();
            } else if (ArrayNameToEmpty.equals("BatchRunReportArray") || ArrayNameToEmpty.equals("BATCHREPORTARRAY")) {
                BatchRunReportArray.clear();
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static String FrmSubRightPad(String text, int length) {
        try {
            return String.format("%-" + length + "." + length + "s", text);

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return e.toString();
        }
    }

    public static String FrmDateTimeStringFormat(String FormatOutputMask, String FormatInputMask, String InputDateTimeString, String OutDateTimeString, String ExtraParam) throws Exception {
        try {
            LocalDateTime dateTimeObject = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter;
            String dateTime = null;

            if (FormatOutputMask.equals("DATETIMESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblDateTimeFormat").split("~")[0]);
            } else if (FormatOutputMask.equals("TIMESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblTimeFormat").split("~")[0]);
            } else if (FormatOutputMask.equals("DATESTAMP")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(currentGlobalParams.getProperty("GblDateFormat").split("~")[0]);
            } else {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FormatOutputMask);
            }
            dateTime = dateTimeObject.format(dateTimeFormatter);
            if (OutDateTimeString != null && dateTime != null) {
                if (array.get("TestParams") == null) {
                    array.put("TestParams", new HashMap<>());
                }
                if (array.get("TestParams").containsKey(OutDateTimeString)) {
                    array.get("TestParams").replace(OutDateTimeString, dateTime);
                } else {
                    array.get("TestParams").put(OutDateTimeString, dateTime);
                }
            }
            return dateTime;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "false";
        }
    }

    public static String FrmSubDynamicArrayValueGet(String DataSheetName, String Key) {
        return array.get(DataSheetName).getOrDefault(Key, "NULL");
    }

    public static boolean FrmSubRowToSkipCheck(String DatasheetName, int position, String ExtraParam) {
        try {
            if (arrayList.get("BatchTestRunList").get(position).get("RowToSkipFlag") != null) {
                String str = arrayList.get("BatchTestRunList").get(position).get("RowToSkipFlag");
                if (!str.trim().equals("NULL")) {
                    String[] rowToSkipTags = str.split("\\|");
                    for (int j = 0; j < rowToSkipTags.length; j++) {
                        List<String> tagList = new ArrayList<>();
                        if (rowToSkipTags[j].contains(":")) {
                            String sheetName = rowToSkipTags[j].split(":")[0].trim();
                            String[] sheetNameTags = rowToSkipTags[j].split(":")[1].trim().split("\\,");
                            for (int k = 0; k < sheetNameTags.length; k++) {
                                tagList.add(sheetNameTags[k]);
                            }
                            if (sheetName.equals("DEFAULT")) {
                                rowToSkipArray.put("TestMain", tagList);
                            } else {
                                rowToSkipArray.put(sheetName, tagList);
                            }
                        } else {
                            String[] sheetNameTags = rowToSkipTags[j].trim().split("\\,");
                            for (int k = 0; k < sheetNameTags.length; k++) {
                                tagList.add(sheetNameTags[k]);
                            }
                            rowToSkipArray.put("TestMain", tagList);
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(e.getMessage(), null);
            return false;

        }
    }

    public static boolean FrmSubCheckDataSheetLoad(String DataSheetName, String ExtraParam) {
        if (arrayList.get(DataSheetName) != null) {
            if (arrayList.get(DataSheetName).size() > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static String FrmSubCellParamParse(String Param, String ExtraParam) {
        try {
            if (Param != null) {
                if (Param.contains("|") || Param.contains("Ӿ")) {
                    if (Param.startsWith("\"") || Param.endsWith("\"")) {
                        Param = FrmSubDelimitedParamParse(Param, ExtraParam);
                    }
                }
                Param = FrmParamParse(Param, ExtraParam);
            }

            return Param;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "FAIL";
        }
    }

    public static String FrmSubDelimitedParamParse(String Param, String ExtraParam) {
        try {
            String value = "";
            Param = Param.replace("\\\"", "&quot;");
            Param = Param.replace("\"", "");
            Param = Param.replace("&quot;", "\"");
            if (Param.contains("|")) {
                String[] splitedParams = Param.split("\\|");
                for (String splitedParam : splitedParams) {
                    String splitedParamValue = "";
                    if (splitedParam.contains("`")) {
                        splitedParam = FrmSubParameterValueReplace(splitedParam, "`([^`]*)`", ExtraParam);
                    }
                    if (splitedParam.contains(":")) {
                        splitedParam = splitedParam.replace("\\:", "&quot;");
                        if (splitedParam.trim().equals(splitedParam.trim().toUpperCase())) {
                            splitedParam = splitedParam.replace("&quot;", "\\:");
                            value = value + splitedParam + "|";
                        } else if (splitedParam.split(":")[1].trim().equals(splitedParam.split(":")[1].trim().toUpperCase())) {
                            splitedParam = splitedParam.replace("&quot;", "\\:");
                            value = value + splitedParam + "|";
                        } else {
                            splitedParamValue = FrmParamParse(splitedParam.split(":")[1].replace("&quot;", "\\:"), ExtraParam);
                            value = value + splitedParam.split(":")[0].replace("&quot;", ":") + ":" + splitedParamValue + "|";
                        }
                    } else if (splitedParam.contains("=")) {
                        splitedParam = splitedParam.replace("\\=", "&quot;");
                        if (splitedParam.trim().equals(splitedParam.trim().toUpperCase())) {
                            splitedParam = splitedParam.replace("&quot;", "=");
                            value = value + splitedParam + "|";
                        } else if (splitedParam.split("=")[1].trim().equals(splitedParam.split("=")[1].trim().toUpperCase())) {
                            splitedParam = splitedParam.replace("&quot;", "=");
                            value = value + splitedParam + "|";
                        } else {
                            splitedParamValue = FrmParamParse(splitedParam.split("=")[1].replace("&quot;", "="), ExtraParam);
                            value = value + splitedParam.split("=")[0].replace("&quot;", "=") + "=" + splitedParamValue + "|";
                        }
                    } else {
                        splitedParamValue = FrmParamParse(splitedParam, ExtraParam);
                        value = value + splitedParamValue + "|";
                    }
                }
            } else if (Param.contains("Ӿ")) {
                delimitedParameters = Param.split("Ӿ");
                for (Object obj : delimitedParameters) {
                    String splitedParamValue = "";
                    splitedParamValue = FrmParamParse(obj.toString(), ExtraParam);
                    value = value + splitedParamValue + "Ӿ";
                }
            }
            Param = value.substring(0, value.length() - 1);
            return Param;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "FAIL";
        }
    }

    public static String FrmParamParse(String Param, String ExtraParam) {
        if (Param != null) {
            if (Param.startsWith("Gbl")) {
                for (Object gblParams : currentGlobalParams.keySet()) {
                    if (Param.equals(gblParams.toString())) {
                        Param = currentGlobalParams.getProperty(gblParams.toString()).split("~")[0].trim();
                    }
                }
            } else if (Param.startsWith("Cov")) {
                if (array.get("CarryOverParams") != null) {
                    for (Object paramValue : array.get("CarryOverParams").keySet()) {
                        if (Param.equals(paramValue.toString())) {
                            Param = array.get("CarryOverParams").get(paramValue);
                        }
                    }
                }
            } else if (Param.startsWith("\"") && Param.endsWith("\"")) {
                Param = Param.replace("\\\"", "&quot;");
                Param = Param.replace("\"", "");
                Param = Param.replace("&quot;", "\"");
                if (Param.contains("`")) {
                    Param = FrmSubParameterValueReplace(Param, "`([^`]*)`", ExtraParam);
                }
            } else if (Param.equals(Param.toUpperCase()) && !Param.startsWith("$")) {
                Param = Param.toUpperCase();
            } else if (Param.trim().equalsIgnoreCase("\\t") || Param.trim().equalsIgnoreCase("TAB")) {
                Param = "\t";
            } else if (Param.trim().equals(" ") || Param.trim().equalsIgnoreCase("SPACE")) {
                Param = " ";
            } else if (Param.trim().equalsIgnoreCase("\\n") || Param.trim().equalsIgnoreCase("NEWLINE")) {
                Param = "\n";
            } else {
                Param = Param.replace("\\\"", "&quot;");
                Param = Param.replace("\"", "");
                Param = Param.replace("&quot;", "\\\"");
                boolean batchParamsFind = true;
                if (array.get("TestParams") != null) {
                    for (Object paramKey : array.get("TestParams").keySet()) {
                        if (Param.equals(paramKey.toString())) {
                            Param = array.get("TestParams").get(paramKey);
                            batchParamsFind = false;
                            break;
                        }
                    }
                }
                if (batchParamsFind) {
                    if (array.get("BatchTestRunParams") != null) {
                        for (Object paramKey : array.get("BatchTestRunParams").keySet()) {
                            if (Param.equals(paramKey.toString())) {
                                Param = array.get("BatchTestRunParams").get(paramKey);
                                break;
                            }
                        }
                    }
                }
            }
            //Checking out parameter with miltiple purpose.
            if (((Param.startsWith("[") && Param.endsWith("]")) || (Param.contains("[") && Param.contains("]")))
                    && (!Param.contains("{") && !Param.contains("}"))) {
                if (Param.startsWith("[") && Param.endsWith("]")) {
                    Param = Param.substring(1, Param.length() - 1);
                    if (array.get("TestParams") != null) {
                        if (array.get("TestParams").containsKey(Param)) {
                            array.get("TestParams").remove(Param);
                        }
                    }
                } else {
                    boolean check = true;
                    if (array.get("TestParams") != null) {
                        if (array.get("TestParams").containsKey(Param)) {
                            check = false;
                        }
                    }
                    if (check) {
                        //For multiple OutParameter in one single parameter, RestApi Sheet purpose.
                        Pattern p = Pattern.compile("\\[(.*?)\\]");
                        Matcher m = p.matcher(Param);
                        while (m.find()) {
                            if (array.get("TestParams") != null) {
                                if (m.group(1).contains(":")) {
                                    if (array.get("TestParams").containsKey(m.group(1).split(":")[0].trim())) {
                                        array.get("TestParams").remove(m.group(1).split(":")[0].trim());
                                    }
                                } else {
                                    if (array.get("TestParams").containsKey(m.group(1))) {
                                        array.get("TestParams").remove(m.group(1));
                                    }
                                }
                            }
                        }
//                        Param = Param.replaceAll("\\[", "").replaceAll("\\]", "");
                    }
                }
            }
            //Single quote check before exit.
            if (Param.contains("`")) {
                Param = FrmSubParameterValueReplace(Param, "`([^`]*)`", ExtraParam);
                //Checking in testparam with replaced value.
                if (array.get("TestParams") != null) {
                    for (Object paramValue : array.get("TestParams").keySet()) {
                        if (Param.equals(paramValue.toString())) {
                            Param = array.get("TestParams").get(paramValue);
                        }
                    }
                }
            }
            Param = Param.replace("\\\"", "&quot;");
            Param = Param.replace("\"", "");
            Param = Param.replace("&quot;", "\"");
        }
        return Param;
    }

    public static String FrmSubParameterValueReplace(String text, String regex, String ExtraParam) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                parameterToBeReplaced = matcher.group(1);
                if (!parameterToBeReplaced.equals(FrmParamParse(parameterToBeReplaced, null)) || parameterToBeReplaced.startsWith("$")) {
                    text = text.replace("`" + parameterToBeReplaced + "`", FrmParamParse(parameterToBeReplaced, null).trim());
                }

            }
            return text;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return e.toString();
        }
    }

    public static String FrmSubPatternMatch(String patternToMatch, String fromString, int group) {
        try {
            output = "";
            Pattern pattern = Pattern.compile(patternToMatch);
            Matcher matcher = pattern.matcher(fromString);
            if (matcher.find()) {
                output = matcher.group(group);
            }
            return output;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "FAIL";
        }
    }

    public static void FrmParamValueTransferToCurrentGblParam(String nameOfTheArray, String ExtraParam) {
        try {
            HashMap<String, String> tempGblArray = new HashMap<>();
            for (Object paramKey : array.get(nameOfTheArray).keySet()) {
                String paramVal = array.get(nameOfTheArray).get(paramKey);
                if (paramVal != null) {
                    paramVal = FrmParamParse(paramVal, ExtraParam);
                    if (paramKey.toString().trim().startsWith("##")) {
                        continue;
                    }
                    if (paramKey.toString().trim().startsWith("Gbl")) {
                        if (currentGlobalParams.containsKey(paramKey.toString().trim())) {
                            currentGlobalParams.replace(paramKey.toString().trim(), paramVal);
                        } else {
                            currentGlobalParams.put(paramKey.toString().trim(), paramVal);
                        }
                        tempGblArray.put(paramKey.toString().trim(), paramVal);
                    } else if (paramKey.toString().trim().startsWith("Cov")) {
                        if (array.get("CarryOverParams") == null) {
                            array.put("CarryOverParams", carryOverParamArray);
                        }
                        if (array.get("CarryOverParams").containsKey(paramKey.toString().trim())) {
                            array.get("CarryOverParams").replace(paramKey.toString().trim(), paramVal);
                        } else {
                            array.get("CarryOverParams").put(paramKey.toString().trim(), paramVal);
                        }
                    }
                }
            }
            for (Object tempGblKey : tempGblArray.keySet()) {
                if (nameOfTheArray.trim().equalsIgnoreCase("TestParams")) {
                    array.get("TestParams").remove(tempGblKey);
                }
            }
            tempGblArray.clear();

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(e.getMessage(), null);
        }
    }

    public static String FrmSubTestRelativeFolderPathCreate(String FilePath, String ExtraParam) {
        try {
            String relativeTestFolderPath;
            relativeTestFolderPath = FrmSubPathConcate(new String[]{JAR_FILE_PATH, FilePath});
            if (ExtraParam != null && ExtraParam.trim().equals("POSITIVECHECK")) {
                if (new File(relativeTestFolderPath).exists()) {
                    return relativeTestFolderPath;
                } else {
                    return FilePath;
                }
            } else {
                if (!new File(relativeTestFolderPath).exists()) {
                    if (new File(relativeTestFolderPath).mkdir()) {
                        return relativeTestFolderPath;
                    } else {
                        return FilePath;
                    }
                } else {
                    return FilePath;
                }
            }
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return "NULL";
        }
    }

    public static String FrmSubPathConcate(String[] paths) {
        File file = new File(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            file = new File(file, paths[i]);
        }
        return file.getPath();
    }

    public static boolean FrmSubRowToSkipMatch(String DatasheetName, String RowMarkingFlag, String ExtraParam) {
        try {
            if (RowMarkingFlag != null && DatasheetName != null) {
                List<String> rowToSkipList;
                if (!RowMarkingFlag.trim().equals("NULL") && !DatasheetName.trim().equals("NULL")) {
                    if (DatasheetName.trim().equals("DEFAULT")) {
                        rowToSkipList = rowToSkipArray.get("TestMain");
                    } else {
                        rowToSkipList = rowToSkipArray.get(DatasheetName.trim());
                    }
                    if (rowToSkipList != null && rowToSkipList.contains(RowMarkingFlag)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (DatasheetName != null) {
                if (DatasheetName.trim().equals("DEFAULT")) {
                    if (!rowToSkipSheetName.contains("TestMain")) {
                        rowToSkipSheetName.add("TestMain");
                    }
                } else {
                    if (!rowToSkipSheetName.contains(DatasheetName.trim())) {
                        rowToSkipSheetName.add(DatasheetName.trim());
                    }
                }
            }
            return false;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(e.getMessage(), null);
            return false;
        }
    }

    //Functionality to parse parameters with actual value from excel sheet
    public static boolean FrmSubMethodParamListParse(String ParamList, String OutParamList, String ExtraParam) {
        try {
            if (ParamList != null) {
//                parameters = ParamList.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                parameters = FrmSubParamSplit(ParamList, ',', null).toArray();
                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].toString().contains("|") || parameters[i].toString().contains("Ӿ")) {
                        if (!parameters[i].toString().startsWith("\"") || !parameters[i].toString().endsWith("\"")) {
                            parameters[i] = FrmSubDelimitedParamParse(parameters[i].toString(), ExtraParam);
                        }
                    }
                    parameters[i] = FrmParamParse(parameters[i].toString(), ExtraParam);
                }
                if (array.get("TestParams") != null) {
                    if (array.get("TestParams").containsKey(OutParamList)) {
                        array.get("TestParams").replace(OutParamList, Arrays.toString(parameters));
                    } else {
                        array.get("TestParams").put(OutParamList, Arrays.toString(parameters));
                    }
                }
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static List<String> FrmSubParamSplit(String orig, char delimeter, String ExtraParam) {
        List<String> splitted = new ArrayList<>();
        int nextingLevel = 0, nextQuote = 0;
        boolean firstTime = true;
        StringBuilder result = new StringBuilder();
        for (char c : orig.toCharArray()) {
            if (c == delimeter && nextingLevel == 0 && nextQuote == 0) {
                splitted.add(result.toString());
                result.setLength(0);// clean buffer
            } else {
                //opposite sign pair
                if (c == '(' || c == '[' || c == '{')
                    nextingLevel++;
                if (c == ')' || c == ']' || c == '}')
                    nextingLevel--;

                //no opposite sign
                if (firstTime) {
                    //if want signle quote then add c == '\'', it is removed for report msg issue from datasheet.
                    if (c == '"' || c == '`') {
                        nextQuote++;
                        firstTime = false;
                    }
                } else {
                    if (c == '"' || c == '`') {
                        nextQuote--;
                        firstTime = true;
                    }
                }
                result.append(c);
            }
        }
        splitted.add(result.toString());
        return splitted;
    }

    //Framework internal invoke method functionality to execute methods from excel sheet
    public static boolean FrmSubMethodInvoke(String MethodName, Object[] ParamList, String ParsedFlag, String ExtraParam) {
        try {
            GblPrevMethodReturnCode = GblLastMethodReturnCode;
            if (GblLastMethodReturnCode) {
                currentGlobalParams.setProperty("GblPrevMethodReturnCode", "TRUE");
            } else {
                currentGlobalParams.setProperty("GblPrevMethodReturnCode", "FALSE");
            }
            if (ParsedFlag.equals("TRUE")) {
                FrmSubMethodParamListParse(ParamList.toString(), "OutParamList", null);
                ParamList = parameters;
            }
            Class<?> params[] = new Class[ParamList.length];
            for (int i = 0; i < ParamList.length; i++) {
                if (ParamList[i] instanceof Integer) {
                    params[i] = Integer.TYPE;
                } else if (ParamList[i] instanceof String) {
                    params[i] = String.class;
                }
            }
            System.out.println(MethodName);
            String className = "com.automation.ui.methods.FunctionalMethods";
            Class<?> cls = Class.forName(className);
            Method myMethod = cls.getMethod(MethodName, params);
            result = (Boolean) myMethod.invoke(null, ParamList);
            GblLastMethodReturnCode = result;
            return result;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    //Browser launch functionality
    public static WebDriver FrmSubBrowserDriverSetUp(String browser) throws Exception {

        try {
            BROWSERHEADLESS = false;
            if (currentGlobalParams.getProperty("GblHeadless").split("~")[0].trim().equalsIgnoreCase("True") ||
                    currentGlobalParams.getProperty("GblHeadless").split("~")[0].trim().equalsIgnoreCase("Yes")) {
                BROWSERHEADLESS = true;
            }
            switch (browser) {
                case "CHROME":
                    WebDriverManager.chromedriver().setup();
                    if (BROWSERHEADLESS) {
                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("headless");
                        driver = new ChromeDriver(options);
                    } else {
                        driver = new ChromeDriver();
                    }
//                    driver.manage().window().maximize();
//                    browserName = ((ChromeDriver) driver).getCapabilities().getCapability("browserName").toString().toUpperCase();
//                    browserVersion = ((ChromeDriver) driver).getCapabilities().getCapability("browserVersion").toString();
                    return driver;
                case "MSEDGE":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    return driver;
                case "MSIE":
                    WebDriverManager.iedriver().setup();
                    driver = new InternetExplorerDriver();
                    return driver;
//                case "OPERA":
//                    WebDriverManager.operadriver().setup();
//                    driver = new OperaDriver();
//                    return driver;
                case "FIREFOX":
                    WebDriverManager.firefoxdriver().setup();
                    if (BROWSERHEADLESS) {
                        FirefoxOptions options = new FirefoxOptions();
                        options.setHeadless(true);
                        driver = new FirefoxDriver(options);
                    } else {
                        driver = new FirefoxDriver();
                    }
                    return driver;
                default:
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                    return driver;
            }
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            throw new Exception(e);
        }
    }

    public static boolean FrmSubIsElementPresent(By locatorKey) {
        try {
            driver.findElement(locatorKey);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public static void FrmSubSwitchToCurrentWindow() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    public static boolean FrmSubObjectActionSet(String ObjectName, String ActionType, String ValueToBeSet, String ExtraParam) {
        try {
            switch (FOUNDOBJECT.toUpperCase()) {
                case "TEXTBOX":
                    switch (ActionType.toUpperCase()) {
                        case "DEFAULT":
                        case "TEXTINPUT":
                        case "VALUESET":
                            if (ValueToBeSet.equals("BLANK")) {
                                action.moveToElement(webElement).perform();
                                webElement.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                            } else {
                                //      action.moveToElement(webElement).perform();
                                webElement.sendKeys(ValueToBeSet);
                                if (AUXKEY != null) {
                                    switch (AUXKEY.toUpperCase()) {
                                        case "ENTER":
                                        case "RETURN":
                                            webElement.sendKeys(Keys.ENTER);
                                            break;
                                    }
                                }
                            }
                            break;
                        case "LCLICK":
                            action.moveToElement(webElement).click().build().perform();
                            break;
                        case "RCLICK":
                            action.contextClick(webElement).perform();
                            break;
                        case "MOUSEOVER":
                            action.moveToElement(webElement).perform();
                            break;
                        case "SETBLANK":
                            action.moveToElement(webElement).click().build().perform();
                            action.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                            break;
                        case "null":
                            message = "Action type can't be null!";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;
                        default:
                            message = "Action type is incorrect!";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;

                    }
                    break;
                case "MENUBUTTON":
                case "PUSHBUTTON":
                    switch (ActionType.toUpperCase()) {
                        case "DEFAULT":
                        case "LCLICK":
                            webElement.click();
                            break;
                    }
                    break;
                case "LISTBOX":
                    switch (ActionType.toUpperCase()){
                        case "DEFAULT":
                            webElement.click();
                            partialXpath = utilityObjects.getProperty("GuiListBox2").split("~");
                            if(FrmSubIsElementPresent(By.xpath(objectProperties[1]+"//following::div[@role='listbox']"))){
                               WebElement element = driver.findElement(By.xpath(objectProperties[1]+partialXpath[1]+ValueToBeSet+partialXpath[2]));
                                if(element.getText().contains(ValueToBeSet)){
                                    element.click();
                                }
                            }
                    }
                case "DATACELL":
                    switch(ActionType.toUpperCase()){
                        case "DEFAULT":
                        case "LCLICK":
                            webElement.click();
                    }
            }

            return true;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static void FrmSubScrollToElement(WebDriver driver, WebElement webElement) {
        int x = webElement.getLocation().getX(), y = webElement.getLocation().getY() - 120;
        String scroll_by_coord = "window.scrollTo(" + x + "," + y + ");";
//                scroll_nav_out_of_way = "window.scrollBy(0, -120);";
        ((JavascriptExecutor) driver).executeScript(scroll_by_coord);
//        ((JavascriptExecutor) driver).executeScript(scroll_nav_out_of_way);
    }

}
