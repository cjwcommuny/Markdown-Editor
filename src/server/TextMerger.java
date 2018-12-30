package server;

import plaintext.diff_match_patch;

import java.util.LinkedList;

public class TextMerger {
    private static diff_match_patch dmp = new diff_match_patch();

    public static String merge(String originalText, String text1, String text2) {
        LinkedList<diff_match_patch.Patch> patch = dmp.patch_make(originalText, text1);
        Object[] returnValue = dmp.patch_apply(patch, text2);
        return (String) returnValue[0];
    }

    public static String merge(String originalText, String text) {
        LinkedList<diff_match_patch.Patch> patch = dmp.patch_make(originalText, text);
        Object[] returnValue = dmp.patch_apply(patch, text);
        return (String) returnValue[0];
    }
}
