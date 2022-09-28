package com.automation.ui;

import com.automation.ui.methods.FunctionalMethods;

import java.util.Iterator;

public class TestEngine extends FunctionalMethods {
    public static void main(String args[]) {
        FrmSubRepositoryLoad();
        if (args.length != 0) {

        } else {
            testsFolderPath = currentGlobalParams.getProperty("GblTestRunListFolderPath").split("~")[0];
            testsToRunListFile = currentGlobalParams.getProperty("GblTestsToRunListFile").split("~")[0];
            System.out.println("test" + testsFolderPath);
        }
        ExcelSheetDataFetch(testsToRunListFile, testsFolderPath, "BatchTestRunParams", "PARSE:TRUE");
        ExcelSheetDataFetch(testsToRunListFile, testsFolderPath, "BatchTestRunList", "PARSE:TRUE");
        BatchRunReportArray.clear();
        FrmSubTestHeaderFooterSet("BATCHHEADER", null);
        batchReportFolderPath = currentGlobalParams.getProperty("GblBatchReportFolderPath").split("~")[0].trim();
        batchTestRunReportFile = currentGlobalParams.getProperty("GblBatchTestRunReportFile").split("~")[0].trim();
        if (BatchRunReportArray.size() > 0) {
            FrmSubFileDirectoryGeneration(batchReportFolderPath, batchTestRunReportFile);
            TextOutputLineWrite("BATCHREPORTARRAY", null, batchTestRunReportFile, null, batchReportFolderPath, null);
            FrmSubDataArrayClear("BatchRunReportArray", null);
        }

        for (int testDataFileIndex = 0; testDataFileIndex < arrayList.get("BatchTestRunList").size(); testDataFileIndex++) {
            FrmSubRowToSkipCheck("BatchTestRunList", testDataFileIndex, null);
            RowMarkingFlag = arrayList.get("BatchTestRunList").get(testDataFileIndex).get("RowMarkingFlag");
            if (RowMarkingFlag == null) {
                continue;
            } else if (RowMarkingFlag.startsWith("##") || RowMarkingFlag.equals("ROWSKIP")) {
                String stopIndicator = "";
                stopIndicator = FrmSubPatternMatch("##\\s*([(A-Za-z)]*)\\s*", RowMarkingFlag, 1);
                if (stopIndicator.trim().equalsIgnoreCase("EOF")) {
                    break;
                }
                continue;
            } else {
                testDataIndex = testDataFileIndex;
                serialNo++;
                TestDataFileName = arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestDataFileName");
                TestDataFilePath = arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestDataFilePath");

                TestResultReportArray.clear();
                TestDetailLogArray.clear();

                currentGlobalParams.setProperty("GblTestReportFolderPath", arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestReportFolderPath"));
                currentGlobalParams.setProperty("GblTestLogFolderPath", arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestLogFolderPath"));
                currentGlobalParams.setProperty("GblScrnShotFolderPath", arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestScreenShotFolderPath"));

                ExcelSheetDataFetch(TestDataFileName, TestDataFilePath, "TestParams", "PARSE:TRUE");
//                if (array.get("TestParams") != null) {
//                    if (array.get("TestParams").containsKey("$LOOPNUM")) {
//                        array.get("TestParams").replace("$LOOPNUM", "0");
//                    } else {
//                        array.get("TestParams").put("$LOOPNUM", "0");
//                    }
//                }
                ExcelSheetDataFetch(TestDataFileName, TestDataFilePath, "TestMain", null);
                //  FrmSubArrayParamsDatasheetLoad(TestDataFileName, TestDataFilePath, "ArrayParams", null);
                if (currentGlobalParams.getProperty("GblTrimSetting").split("~")[0].equalsIgnoreCase("TRUE") ||
                        currentGlobalParams.getProperty("GblTrimSetting").split("~")[0].equalsIgnoreCase("YES")) {
                    for (Object paramKey : array.get("TestParams").keySet()) {
                        array.get("TestParams").replace(paramKey.toString(), array.get("TestParams").get(paramKey).trim());
                    }
                }
                FrmParamValueTransferToCurrentGblParam("BatchTestRunParams", null);
                FrmParamValueTransferToCurrentGblParam("TestParams", null);
//                if (CURRENT_RUNNING_ENVIRONMENT != null) {
//                    currentGlobalParams.replace("GblAppWebUrl", CURRENT_RUNNING_ENVIRONMENT);
//                }

                testTitleName = arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TestTitleName");
                if (testTitleName == null) {
                    testTitleName = TestDataFileName;
                }

                testReportFolderPath = FrmSubTestRelativeFolderPathCreate(currentGlobalParams.getProperty("GblTestReportFolderPath").split("~")[0], null);
                testLogFolderPath = FrmSubTestRelativeFolderPathCreate(currentGlobalParams.getProperty("GblTestLogFolderPath").split("~")[0], null);
                screenShotFolderPath = FrmSubTestRelativeFolderPathCreate(currentGlobalParams.getProperty("GblScrnShotFolderPath").split("~")[0], null);

                testReportFile = currentGlobalParams.getProperty("GblTestReportFile").split("~")[0];
                testLogFile = currentGlobalParams.getProperty("GblTestLogFile").split("~")[0];
                testerName = arrayList.get("BatchTestRunList").get(testDataFileIndex).get("TesterName");

                FrmSubTestHeaderFooterSet("TESTHEADER", null);
                FrmSubFileDirectoryGeneration(testLogFolderPath, testLogFile);
                FrmSubFileDirectoryGeneration(testReportFolderPath, testReportFile);

                for (int methodNameIndex = 0; methodNameIndex < arrayList.get("TestMain").size(); methodNameIndex++) {
//                    FrmSubExceptionHandler();
//                    currentGlobalParams.setProperty("GblSystemSpecificError", "N/A");
//                    currentGlobalParams.setProperty("GblFrmErrorMessage", "");
                    String MethodName = arrayList.get("TestMain").get(methodNameIndex).get("MethodName").trim();
                    TestReportFlag = arrayList.get("TestMain").get(methodNameIndex).get("TestReportFlag");
                    if (TestReportFlag == null) {
                        TestReportFlag = "NO";
                    }

                    MiscComments = arrayList.get("TestMain").get(methodNameIndex).get("MiscComments");
                    if (rowToSkipArray.size() > 0) {
                        if (FrmSubRowToSkipMatch("TestMain", arrayList.get("TestMain").get(methodNameIndex).get("RowMarkingFlag"), null)) {
                            continue;
                        }
                    }
                    if (MethodName == null) {
                        continue;
                    } else if (MethodName.startsWith("@@")) {

                        currentGlobalParams.setProperty("GblCurrentTestStepTag", arrayList.get("TestMain").get(methodNameIndex).get("MethodName").replace("@@", "").trim());
                        currentGlobalParams.setProperty("GblStepPassFailStatus", "PASS");

                        if (currentGlobalParams.getProperty("GblTextOutputLinePrint").split("~")[0].trim().equals("TESTSTEPEXIT")) {
                            if (TestDetailLogArray.size() > 0) {
                                TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);
                                FrmSubDataArrayClear("TestDetailLogArray", null);
                            }
                            if (TestResultReportArray.size() > 0) {
                                TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                                FrmSubDataArrayClear("TestResultReportArray", null);
                            }
                        }
                        message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                + " Test Step: " + currentGlobalParams.getProperty("GblCurrentTestStepTag") + "\n";
                        FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                        message = "";
                        continue;
                    } else if (MethodName.startsWith("##") || MethodName.trim().equals("ROWSKIP")) {
                        String stopIndicator = "";
                        stopIndicator = FrmSubPatternMatch("##\\s*([(A-Za-z)]*)\\s*", MethodName, 1);
                        if (stopIndicator.trim().equalsIgnoreCase("EOF")) {
                            break;
                        }
                        continue;
                    } else {
                        RowNumber = "<TestMain Row " + String.format("%03d", methodNameIndex + 2) + "> ";
                        message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                + " " + FrmSubRightPad("Call Entry Pre Parse", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + RowNumber + MethodName + "::\t" + arrayList.get("TestMain").get(methodNameIndex).get("ParameterList") + ".\n";
                        FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                        message = "";

                        FrmSubMethodParamListParse(arrayList.get("TestMain").get(methodNameIndex).get("ParameterList"), "OutParamList", null);
                        message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                + " " + FrmSubRightPad("Call Entry Post Parse", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + RowNumber + MethodName + "::\t" + array.get("TestParams").get("OutParamList") + ".\n";
                        FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                        message = "";

                        FrmSubMethodInvoke(MethodName, parameters, "FALSE", null);


                        if (!GblLastMethodReturnCode) {

                            currentGlobalParams.setProperty("GblStepPassFailStatus", "FAIL");
                            currentGlobalParams.setProperty("GblOverallPassFailStatus", "FAIL");

                            message = currentGlobalParams.get("GblCurrentTestStepTag") + ": " + MethodName + "::\tReturn Code --> FAIL.\n";
                            FrmSubTextMsgArrayInsert("TestResultReportArray", message, null);
                            message = "";

                            message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                    + " " + FrmSubRightPad("Call Exit", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + RowNumber + MethodName + "::\tReturn Code --> FAIL.\n";
                            FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                            message = "";

                            if (currentGlobalParams.getProperty("GblSystemSpecificError").split("~")[0].trim().toUpperCase().equals("TRUE")) {
                                String SuffixName = "ERROR_" + FrmSubDateTimeStringFormat("yyyyMMddHHmmssSSSS", null, null, null, null);
                                ScreenShotCapture("FULLSCREEN", "DEFAULT", "DEFAULT", SuffixName, "DEFAULT", null);
                                currentGlobalParams.setProperty("GblSystemSpecificError", "FALSE");
                            }
                        } else {

                            message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                    + " " + FrmSubRightPad("Call Exit", Integer.parseInt(currentGlobalParams.getProperty("GblTestReportFormatMask").split("~")[0])) + ":\t" + RowNumber + MethodName + "::\tReturn Code --> PASS.\n";
                            FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                            message = "";

                            if (TestReportFlag.equals("TRUE") || TestReportFlag.equals("YES")) {
                                if (MiscComments == null) {
                                    message = currentGlobalParams.get("GblCurrentTestStepTag") + ": " + MethodName + "::\t" + array.get("TestParams").get("OutParamList") + "\tReturn Code --> PASS.\n";
                                } else if (!MiscComments.contains("RMI:")) {
                                    message = currentGlobalParams.get("GblCurrentTestStepTag") + ": " + MethodName + "::\t" + array.get("TestParams").get("OutParamList") + "\tReturn Code --> PASS.\n";
                                } else if (MiscComments.contains("RMI:")) {
                                    MiscComments = FrmSubParameterValueReplace(MiscComments, "`([^`]*)`", null);
                                    MiscComments = FrmSubParameterValueReplace(MiscComments, "\\[([^\\[]*)\\]", null);
                                    MiscComments = MiscComments.replace("RMI:", "").trim();
                                    message = currentGlobalParams.get("GblCurrentTestStepTag") + ": " + MethodName + "::\t" + MiscComments + "\tReturn Code --> PASS.\n";
                                }
                                FrmSubTextMsgArrayInsert("TestResultReportArray", message, null);
                                message = "";
                            }
                        }
                        if (currentGlobalParams.getProperty("GblTextOutputLinePrint").split("~")[0].trim().equals("METHODCALLEXIT")) {
                            if (TestDetailLogArray.size() > 0) {
                                TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);
                                FrmSubDataArrayClear("TestDetailLogArray", null);
                            }
                            if (TestResultReportArray.size() > 0) {
                                TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                                FrmSubDataArrayClear("TestResultReportArray", null);
                            }
                        }
                        if (currentGlobalParams.getProperty("GblStepPassFailStatus").split("~")[0].equals("FAIL") && currentGlobalParams.getProperty("GblTestStopper").split("~")[0].equals("TRUE")) {

                            message = "Test Exit:\tEnd Of Test. Overall status FAIL.\n";
                            FrmSubTextMsgArrayInsert("TestResultReportArray", message, null);
                            message = "";

                            message = FrmSubDateTimeStringFormat("TIMESTAMP", "NULL", "NULL", null, null)
                                    + " Test Exit:\tEnd Of Test. Overall status FAIL.\n";
                            FrmSubTextMsgArrayInsert("TestDetailLogArray", message, null);
                            message = "";

                            TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                            TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);

                            FrmSubDataArrayClear("TestResultReportArray", null);
                            FrmSubDataArrayClear("TestDetailLogArray", null);

                            break;
                        }

                    }

                    if (rowToSkipArray.size() > 0) {
                        for (String rowToSkipSheet : rowToSkipSheetName) {
                            if (rowToSkipArray.containsKey(rowToSkipSheet)) {
                                rowToSkipArray.remove(rowToSkipSheet);
                            }
                        }
                        if (rowToSkipArray.size() > 0) {
                            for (String key : rowToSkipArray.keySet()) {
                                message = key + " dataSheet does not found in " + TestDataFileName + "!";
                                FrmSubErrorMsgArrayInsert(message, null);
                                message = "";
                                if (TestDetailLogArray.size() > 0) {
                                    TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);
                                    FrmSubDataArrayClear("TestDetailLogArray", null);
                                }
                                if (TestResultReportArray.size() > 0) {
                                    TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                                    FrmSubDataArrayClear("TestResultReportArray", null);
                                }
                            }
                        }
                    }
                }

                FrmSubTestHeaderFooterSet("TESTFOOTER", null);

                TextOutputLineWrite("TESTREPORTARRAY", null, testReportFile, null, testReportFolderPath, null);
                TextOutputLineWrite("TESTLOGARRAY", null, testLogFile, null, testLogFolderPath, null);

                FrmSubDataArrayClear("TestResultReportArray", null);
                FrmSubDataArrayClear("TestDetailLogArray", null);

                message = " " + FrmSubRightPad("\t\t\t" + Integer.toString(serialNo) + ".\t", 15) + FrmSubRightPad(testTitleName, 50) + FrmSubRightPad(testerName, 20) + FrmSubRightPad(currentGlobalParams.getProperty("GblOverallPassFailStatus").split("~")[0] + ".", 22) + "\n";
                FrmSubTextMsgArrayInsert("BatchRunReportArray", message, null);
                message = "";

                if (currentGlobalParams.getProperty("GblAppLogoutClosure").split("~")[0].trim().equalsIgnoreCase("TRUE")
                        || currentGlobalParams.getProperty("GblAppLogoutClosure").split("~")[0].trim().equalsIgnoreCase("YES")) {
                    BrowserTabClose("DEFAULT", "NULL", "NULL");
                    // BrowserTabSwitch("GuiAppHomePageTab", "DEFAULT", "NULL");
                    UtlSyncTimeWait("1", null);
                    AppLogout("HOMEPAGE", "YES", null);
                    UtlSyncTimeWait("1", null);
                    BrowserWindowClose("DEFAULT", null);

                }
                currentGlobalParams = FrmSubObjectRepositoryGet("GlobalParams");
                logcount = 0;
                reportcount = 0;
                rowToSkipArray.clear();


            }
            //Destroying all arrays without CarryOverParams array.
            for (Iterator<String> arrayKeys = array.keySet().iterator(); arrayKeys.hasNext(); ) {
                String arrayKey = arrayKeys.next();
                if (arrayKey.equals("CarryOverParams") || arrayKey.equals("BatchTestRunParams")) {
                    continue;
                }
                array.remove(arrayKey);
            }
            for (Iterator<String> arrayListKeys = arrayList.keySet().iterator(); arrayListKeys.hasNext(); ) {
                String arrayListKey = arrayListKeys.next();
                if (arrayListKey.equals("BatchTestRunParams") || arrayListKey.equals("BatchTestRunList")) {
                    continue;
                }
                arrayList.get(arrayListKey).clear();
            }
            arrayOfDataSheet.clear();
        }
        FrmSubTestHeaderFooterSet("BATCHFOOTER", null);
        TextOutputLineWrite("BATCHREPORTARRAY", null, batchTestRunReportFile, null, batchReportFolderPath, null);
        FrmSubDataArrayClear("BatchRunReportArray", null);
    }
}

