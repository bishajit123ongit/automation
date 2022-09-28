package com.automation.ui.methods;

public class UtilityMethods extends FrameworkSubRoutine {

    //Generic functionality for page to load
    public static boolean UtlSyncTimeWait(String SecondsToWait, String ExtraParam) {
        int GblMaxTimeWait = Integer.parseInt(currentGlobalParams.getProperty("GblMaxWaitTime").split("~")[0]);
        try {
            if (SecondsToWait.equals("DEFAULT")) {
                Thread.sleep(GblMaxTimeWait * 1000);
            } else {
                Thread.sleep((Integer.parseInt(SecondsToWait) * 1000));
            }
            return true;

        } catch (InterruptedException e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }
}


