package net.qiujuer.genius.ui.widget.attribute;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

/**
 * This use to widget font attribute
 */
public class FontAttribute {
    public static final String[] DEFAULT_FONT_FAMILY = new String[]{"roboto", "opensans"};
    public static final String[] DEFAULT_FONT_WEIGHT = new String[]{"bold", "extrabold", "extralight", "light", "regular"};
    public static final String DEFAULT_FONT_EXTENSION = "ttf";

    private String fontFamily = DEFAULT_FONT_FAMILY[0];
    private String fontWeight = DEFAULT_FONT_WEIGHT[3];
    private String fontExtension = DEFAULT_FONT_EXTENSION;
    private String fontPath = null;

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        if (fontFamily != null && !fontFamily.equals("") && !fontFamily.equals("null"))
            this.fontFamily = fontFamily;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        if (fontWeight != null && !fontWeight.equals("") && !fontWeight.equals("null"))
            this.fontWeight = fontWeight;
    }

    public String getFontExtension() {
        return fontExtension;
    }

    public void setFontExtension(String fontExtension) {
        if (fontExtension != null && !fontExtension.equals("") && !fontExtension.equals("null"))
            this.fontExtension = fontExtension;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    /**
     * Creates and returns the font file from given attributes.
     *
     * @param context Context
     * @return Typeface
     */
    public Typeface getFont(Context context) {
        if (fontPath == null)
            fontPath = "fonts/" + fontFamily
                    + "_" + fontWeight
                    + "." + fontExtension;
        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            Log.e("FontAttribute", "Font file at " + fontPath + " cannot be found or the file is " +
                    "not a valid font file. Please be sure that library assets are included " +
                    "to project. If not, copy assets/fonts folder of the library to your " +
                    "projects assets folder.");
            return null;
        }
    }
}
