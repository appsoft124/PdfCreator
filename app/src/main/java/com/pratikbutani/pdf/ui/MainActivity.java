package com.pratikbutani.pdf.ui;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.pratikbutani.pdf.databinding.ActivityMainBinding;
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
    ActivityMainBinding binding;
    double RevolutionOnWatch, PITCHOFPROPELLER, Enginemiles, Shipmiles = 0.1, slip;
    double Passingtime, EngineSpeed, ShipSpeed, MERPM;
    String inputROW = "", inputpitch = "", inputshipmiles = "", inputtime = ""; // collect input field in string first


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // call initView method here
        initView();


        mContext = getApplicationContext();
        checker = new PermissionsChecker(this);


        dest = getPath();


        binding.buttonCreatePdf.setOnClickListener(view -> {
            createPDF();
        });

    }

    private String getPath() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        return FileUtils.getAppPath(mContext) + currentYear + currentMonth + currentDay + "report.pdf";
    }

    private void openEmail() {
        Intent intent = new Intent(MainActivity.this, EmailActivity.class);
        intent.putExtra("filePath", dest);
        startActivity(intent);
    }


    // initialize view here
    private void initView() {
        // add text change listener to listen any input text
        binding.inputROW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputROW = charSequence.toString();  // collect input when any text change
                verifyInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // add text change list for density
        binding.inputpitch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputpitch = charSequence.toString();  // collect input when any text change
                verifyInput();
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // add text change listener for temperature
        binding.inputshipmiles.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputshipmiles = charSequence.toString();  // collect input when any text change
                verifyInput();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputtime = charSequence.toString();  // collect input when any text change
                verifyInput();
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    // always call this method when there will be any input change
    @SuppressLint("SetTextI18n")
    private void verifyInput() {
        // check if user has entered the input then calculate result
        if (!inputROW.isEmpty() && !inputpitch.isEmpty() && !inputshipmiles.isEmpty()) {
            // everything is ready to calculat
            // convert all string value into double
            RevolutionOnWatch = Double.parseDouble(inputROW);
            PITCHOFPROPELLER = Double.parseDouble(inputpitch);
            Shipmiles = Double.parseDouble(inputshipmiles);

            Enginemiles = RevolutionOnWatch * PITCHOFPROPELLER / 1852;
            slip = ((Enginemiles - Shipmiles) / Enginemiles) * 100;

            // if doesn;t write Passingtime passing time consider as 24
            Passingtime = 24;
            // if write Passingtime then read
            if (!inputtime.isEmpty()) {
                Passingtime = Double.parseDouble(inputtime);
            }

            EngineSpeed = Enginemiles / Passingtime;
            ShipSpeed = Shipmiles / Passingtime;
            MERPM = RevolutionOnWatch / (Passingtime * 60);


            Enginemiles = Math.round(Enginemiles * 10); //rounding the numbers
            Enginemiles = Enginemiles / 10;

            slip = Math.round(slip * 100);//rounding the numbers
            slip = slip / 100;

            EngineSpeed = Math.round(EngineSpeed * 100);//rounding the numbers
            EngineSpeed = EngineSpeed / 100;

            ShipSpeed = Math.round(ShipSpeed * 100);//rounding the numbers
            ShipSpeed = ShipSpeed / 100;

            MERPM = Math.round(MERPM * 10);//rounding the numbers
            MERPM = MERPM / 10;


            binding.txtResult1.setText(Double.toString(Enginemiles));
            binding.txtResult2.setText(Double.toString(slip));

            binding.txtResult3.setText(Double.toString(EngineSpeed));
            binding.txtResult4.setText(Double.toString(ShipSpeed));
            binding.txtResult5.setText(Double.toString(MERPM));

        }

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

            /*
             * Variables for further use....
             */
            Color mColorAccent = new DeviceRgb(153, 204, 255);
            Color mColorBlack = new DeviceRgb(0, 0, 0);
            Color whiteColor = new DeviceRgb(0, 0, 0);
            float mHeadingFontSize = 26.0f;
            float mValueFontSize = 20.0f;


            /*
             * How to USE FONT....
             */
            PdfFont font = PdfFontFactory.createFont("assets/fonts/brandon_medium.otf", "UTF-8", true);


            // Title Order Details...
            addNewIte(document, TextAlignment.CENTER, "Order Details", 40.0f, font, mColorBlack);

            // Fields of Order Details...

            // Adding Chunks for Title and value
            addNewIte(document, TextAlignment.LEFT, "Pitch of Propeller:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, inputpitch, mValueFontSize, font, mColorBlack);
            // add space line
            addSpace(document);



            // Engine Miles [nautical miles] field
            addNewIte(document, TextAlignment.LEFT, "M/E Revolutions on watch:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, inputROW, 40.f, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship Miles [nautical miles] field
            addNewIte(document, TextAlignment.LEFT, "Engine Miles [nautical miles]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, String.valueOf(Enginemiles), mValueFontSize, font, mColorBlack);
            // add space line
            addSpace(document);

            // ship Miles [nautical miles] field
            addNewIte(document, TextAlignment.LEFT, "Ship Miles [nautical miles]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, inputshipmiles, 30.f, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "ship [%]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, String.valueOf(slip), 30.f, font, mColorBlack);
            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Passing time hours:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, inputtime, mValueFontSize, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Engine speed [knotes]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, String.valueOf(EngineSpeed), 30.f, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "Ship,s Speed [knotes]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, String.valueOf(ShipSpeed), 30.f, font, mColorBlack);

            // add space line
            addSpace(document);


            // ship [ ] field
            addNewIte(document, TextAlignment.LEFT, "M/V [rpm]:", mHeadingFontSize, font, mColorAccent);
            // Pitch of Propeller detail
            addNewIte(document, TextAlignment.CENTER, String.valueOf(MERPM), 30.f, font, mColorBlack);

            // add space line
            addSpace(document);

            document.close();

            Toast.makeText(mContext, "Save to " + dest, Toast.LENGTH_SHORT).show();

            PrintPDF(dest);
        } catch (IOException e) {
            LOGE("createPdf: Error " + e.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void PrintPDF(String path) {
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

    private void createPDF() {
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(MainActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        } else {
            createPdf(dest);
        }
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

    // adding space line between order details views
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }


}
