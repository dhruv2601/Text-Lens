package datapole.ocrtext;

import android.Manifest;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class ActivityIntro extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        String[] perm = {android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        addSlide(new SimpleSlide.Builder()
                .title("How To Scan")
                .description("Hold the document so that the text is vertical with respect to you.")
                .image(R.drawable.vertical)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.teal)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Grant Required Permissions")
                .description("We will need a few permissions from you. Please grant them if you haven't already. Thank you and happy scanning.")
                .image(R.drawable.appicon)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.red)
                .permissions(perm)
                .build());
    }
}