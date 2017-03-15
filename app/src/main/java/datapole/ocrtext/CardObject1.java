package datapole.ocrtext;

/**
 * Created by dhruv on 30/12/16.
 */

public class CardObject1 {
    private String mDrawableImage;
    private String txtName;
    private String txtDate;

    CardObject1(String drawableImage, String name, String date) {
        mDrawableImage = drawableImage;
        txtName = name;
        txtDate= date;
    }

    public String getmDrawableImage() {
        return mDrawableImage;
    }

    public void setmDrawableImage(String mDrawableImage) {
        this.mDrawableImage = mDrawableImage;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }
}

