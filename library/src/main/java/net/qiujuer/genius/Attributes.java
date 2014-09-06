package net.qiujuer.genius;

import android.content.res.Resources;
import android.graphics.Color;

/**
 * Created by QiuJu on 2014/9/3.
 */
public class Attributes {

    public static int INVALID = -1;

    public static int DEFAULT_THEME = R.array.blood;

    public static final String DEFAULT_FONT_FAMILY = "roboto";
    public static final String DEFAULT_FONT_WEIGHT = "light";
    public static final String DEFAULT_FONT_EXTENSION = "ttf";
    public static final int DEFAULT_TEXT_APPEARANCE = 0;

    public static int DEFAULT_RADIUS_DP = 4;
    public static int DEFAULT_BORDER_WIDTH_DP = 2;
    public static int DEFAULT_SIZE_DP = 10;

    public static int DEFAULT_RADIUS_PX = 10;
    public static int DEFAULT_BORDER_WIDTH_PX = 4;
    public static int DEFAULT_SIZE_PX = 20;

    /**
     * Color related fields
     */
    private int[] colors;
    private int theme = -1;

    /**
     * Font related fields
     */
    private String fontFamily = DEFAULT_FONT_FAMILY;
    private String fontWeight = DEFAULT_FONT_WEIGHT;
    private String fontExtension = DEFAULT_FONT_EXTENSION;
    private int textAppearance = DEFAULT_TEXT_APPEARANCE;

    /**
     * Size related fields
     */
    private int radius = DEFAULT_RADIUS_PX;
    private int size = DEFAULT_SIZE_PX;
    private int borderWidth = DEFAULT_BORDER_WIDTH_PX;

    private boolean isMaterial = true;
    private boolean isRightAngle = true;

    /**
     * Attribute change listener. Used to redraw the view when attributes are changed.
     */
    private AttributeChangeListener attributeChangeListener;

    public Attributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        this.attributeChangeListener = attributeChangeListener;
        setThemeSilent(DEFAULT_THEME, resources);
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme, Resources resources) {
        setThemeSilent(theme, resources);
        attributeChangeListener.onThemeChange();
    }

    public void setThemeSilent(int theme, Resources resources) {
        try {
            this.theme = theme;
            colors = resources.getIntArray(theme);
        } catch (Resources.NotFoundException e) {

            // setting theme blood if exception occurs (especially used for preview rendering by IDE)
            colors = new int[]{Color.parseColor("#732219"), Color.parseColor("#a63124"),
                    Color.parseColor("#d94130"), Color.parseColor("#f2b6ae")};
        }
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        attributeChangeListener.onThemeChange();
    }

    public int getColor(int colorPos) {
        return colors[colorPos];
    }

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

    public int getRadius() {
        return radius;
    }

    public float[] getOuterRadius() {
        if (isRightAngle)
            return new float[8];
        else
            return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getTextAppearance() {
        return textAppearance;
    }

    public void setTextAppearance(int textAppearance) {
        this.textAppearance = textAppearance;
    }

    public boolean isMaterial() {
        return isMaterial;
    }

    public void setMaterial(boolean isMaterial) {
        this.isMaterial = isMaterial;
    }

    public boolean isRightAngle() {
        return isRightAngle;
    }

    public void setRightAngle(boolean isRightAngle) {
        this.isRightAngle = isRightAngle;
    }

    public interface AttributeChangeListener {
        public void onThemeChange();
    }

}
