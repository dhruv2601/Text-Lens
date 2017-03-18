package datapole.ocrtext;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

/* Copyright: You can use the code as you want, just let me know about it :).
*
*  email: dhruvrathi15@gmail.com
*
*/

public class AboutDeveloper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_developer);

        de.hdodenhof.circleimageview.CircleImageView fbLink = (CircleImageView) findViewById(R.id.myFbLink);
        fbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/profile.php?id=100010247430324"));
                startActivity(intent);
            }
        });

        de.hdodenhof.circleimageview.CircleImageView gitLink = (CircleImageView) findViewById(R.id.myGitLink);
        gitLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/dhruv2601"));
                startActivity(intent);
            }
        });

        ImageView img = (ImageView) findViewById(R.id.web);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://dhruv2601.github.io/"));
                startActivity(intent);
            }
        });
    }
}
