package com.pratikbutani.pdf;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PdfDocAdapter extends PrintDocumentAdapter {

    Context mContext;
    String path;

    public PdfDocAdapter(Context mContext, String path) {
        this.mContext = mContext;
        this.path = path;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        if (cancellationSignal.isCanceled())
            callback.onLayoutCancelled();

        else {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("file name");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();
            callback.onLayoutFinished(builder.build(), !newAttributes.equals(oldAttributes));
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor fileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback callback) {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            File file = new File(path);
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
            byte[] buffer = new byte[16384];
            int size;
            while ((size = inputStream.read(buffer)) >= 0 && !cancellationSignal.isCanceled()) {
                outputStream.write(buffer, 0, size);

            }

                if (cancellationSignal.isCanceled())
                    callback.onWriteCancelled();
                else {
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                }

        } catch (Exception e) {
            callback.onWriteFailed(e.getMessage());
            e.printStackTrace();
            Log.d("PrintPDDF", e.getMessage() + "onWrite");

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                Log.d("PrintPDDF", e.getMessage() + " finaly close stream");
                e.printStackTrace();
            }
        }
    }
}
