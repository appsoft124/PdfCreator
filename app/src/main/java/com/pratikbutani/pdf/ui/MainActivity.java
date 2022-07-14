package com.pratikbutani.pdf.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.fonts.Font;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextChunk;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.pratikbutani.pdf.FileUtils;
import com.pratikbutani.pdf.PdfDocAdapter;
import com.pratikbutani.pdf.R;
import com.pratikbutani.pdf.permission.PermissionsActivity;
import com.pratikbutani.pdf.permission.PermissionsChecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import static com.pratikbutani.pdf.LogUtils.LOGE;
import static com.pratikbutani.pdf.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.pratikbutani.pdf.permission.PermissionsChecker.REQUIRED_PERMISSION;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    PermissionsChecker checker;

    String dest;
    Button sentEmailBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sentEmailBtn = findViewById(R.id.sendEmail);


        setSupportActionBar(toolbar);

        checker = new PermissionsChecker(this);


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

//        Toast.makeText(this, "Today's Date: " + currentYear + currentMonth + currentDay, Toast.LENGTH_SHORT).show();
        dest = FileUtils.getAppPath(mContext) + currentYear + currentMonth + currentDay + "report.pdf";

        sentEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                intent.putExtra("filePath", dest);
                startActivity(intent);
            }
        });
    }

    public void createPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(dest));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            PdfDocumentInfo info = pdfDocument.getDocumentInfo();

            info.setTitle("Example of iText7 by Pratik Butani");
            info.setAuthor("Pratik Butani");
            info.setSubject("iText7 PDF Demo");
            info.setKeywords("iText, PDF, Pratik Butani");
            info.setCreator("A simple tutorial example");

            Document document = new Document(pdfDocument, PageSize.A4, true);

            /***
             * Variables for further use....
             */
            Color mColorAccent = new DeviceRgb(153, 204, 255);
            Color mColorBlack = new DeviceRgb(0, 0, 0);
            Color whiteColor = new DeviceRgb(0, 0, 0);
            float mHeadingFontSize = 26.0f;
            float mValueFontSize = 20.0f;


            /**
             * How to USE FONT....
             */
            PdfFont font = PdfFontFactory.createFont("assets/fonts/brandon_medium.otf", "UTF-8", true);


            // Title Order Details...
            addNewIte(document, TextAlignment.CENTER, "Order Details", 40.0f, font, mColorBlack);

            // Fields of Order Details...

            // Adding Chunks for Title and value
            addNewIte(document, TextAlignment.LEFT, "Pitch of Propeller:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "123123", mValueFontSize, font, mColorBlack);
            // add space line
            addSpace(document);


            // M/E Revolutions on watch field
            addNewIte(document, TextAlignment.LEFT, "M/E Revolutions on watch:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "06/07/2017", mValueFontSize, font, mColorBlack);
            // add space line
            addSpace(document);


            // Engine Miles [nautical miles] field
            addNewIte(document, TextAlignment.LEFT, "M/E Revolutions on watch:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "0.0", 40.f, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship Miles [nautical miles] field
            addNewIte(document, TextAlignment.LEFT, "ship Miles [nautical miles]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "8932", mValueFontSize, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "ship [ ]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "0.00", 40.f, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Passing time hours:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "10:30:50/10/10/2022", mValueFontSize, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Engine speed [knotes]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "0.00", 30.f, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Ship,s Speed [knotes]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "0.00", 30.f, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "M/V [rpm]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, "0.00", 30.f, font, mColorBlack);

            // add space line
            addSpace(document);

            document.close();

            Toast.makeText(mContext, "Save to " + dest, Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            LOGE("createPdf: Error " + e.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void PrintPDDF(String path) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

                PrintDocumentAdapter printDocumentAdapter = new PdfDocAdapter(MainActivity.this, path);

                printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
            }

        } catch (Exception e) {
            Log.d("PrintPDDF", e.getMessage() + " main");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createPDF(View view) {
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(MainActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        } else {
            createPdf(dest);
        }
    }

    public void printPdf(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PrintPDDF(dest);

            }
        }, 1000);
    }


    private void addNewItemLeftRight(Document document, String leftItem, String rightItem) {

        // Fields of Order Details...
        Text textLeft = new Text(leftItem);
        Text textRight = new Text(rightItem);

        Paragraph paragraph = new Paragraph(textLeft);
//        paragraph.add(new Text(rightItem));
        paragraph.add(textRight);
        document.add(paragraph);

    }

    private void addNewIte(Document document, TextAlignment aligment, String text, float fontSize, PdfFont font, Color mColorBlack) {
        Text mTitle = new Text(text).setFont(font).setFontSize(fontSize).setFontColor(mColorBlack);
        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mTitle).setTextAlignment(aligment);
        document.add(mOrderDetailsTitleParagraph);
    }


    void addSpace(Document document) {
        // LINE SEPARATOR
        LineSeparator lineSeparator = new LineSeparator(new DottedLine());
        lineSeparator.setStrokeColor(new DeviceRgb(0, 0, 68));
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));
        // Adding Horizontal Line...
        document.add(lineSeparator);
        // Adding Line Breakable Space....
        document.add(new Paragraph(""));
    }


}
