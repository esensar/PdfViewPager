/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.pdfviewpager.library.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import es.voghdev.pdfviewpager.library.PageFragment;

public class PDFPagerAdapter extends FragmentStateAdapter {
    protected static final int FIRST_PAGE = 0;
    protected static final float DEFAULT_QUALITY = 2.0f;

    protected String pdfPath;
    protected Context context;
    protected PdfRenderer renderer;
    protected BitmapContainer bitmapContainer;
    protected LayoutInflater inflater;

    protected PdfErrorHandler errorHandler = new NullPdfErrorHandler();

    View.OnClickListener pageClickListener;

    public PDFPagerAdapter(@NonNull FragmentActivity fragmentActivity, View.OnClickListener clickListener, String path) {
        super(fragmentActivity);
        context = fragmentActivity;
        pageClickListener = clickListener;
        pdfPath = path;
        init();
    }

    protected void init() {
        try {
            if (pdfPath.startsWith("content://")) {
                renderer = new PdfRenderer(context.getContentResolver().openFileDescriptor(Uri.parse(pdfPath), "r"));
            } else {
                renderer = new PdfRenderer(getSeekableFileDescriptor(pdfPath));
            }

            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            PdfRendererParams params = extractPdfParamsFromFirstPage(renderer);
            bitmapContainer = new SimpleBitmapPool(params);
        } catch (IOException e) {
            errorHandler.onPdfError(e);
        }
    }

    protected PdfRendererParams extractPdfParamsFromFirstPage(PdfRenderer renderer) {
        PdfRenderer.Page samplePage = getPDFPage(renderer, FIRST_PAGE);
        PdfRendererParams params = new PdfRendererParams();

        params.setWidth((int) (samplePage.getWidth() * DEFAULT_QUALITY));
        params.setHeight((int) (samplePage.getHeight() * DEFAULT_QUALITY));

        samplePage.close();

        return params;
    }

    protected ParcelFileDescriptor getSeekableFileDescriptor(String path) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor;

        File pdfCopy = new File(path);

        if (pdfCopy.exists()) {
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY);
            return parcelFileDescriptor;
        }

        if (isAnAsset(path)) {
            pdfCopy = new File(context.getCacheDir(), path);
            parcelFileDescriptor = ParcelFileDescriptor.open(pdfCopy, ParcelFileDescriptor.MODE_READ_ONLY);
        } else {
            URI uri = URI.create(String.format("file://%s", path));
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(uri.toString()), "rw");
        }

        return parcelFileDescriptor;
    }

    protected boolean isAnAsset(String path) {
        return !path.startsWith("/");
    }

    protected PdfRenderer.Page getPDFPage(PdfRenderer renderer, int position) {
        return renderer.openPage(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PageFragment fragment = new PageFragment();

        if (renderer == null || getItemCount() < position) {
            return fragment;
        }

        PdfRenderer.Page page = getPDFPage(renderer, position);

        Bitmap bitmap = bitmapContainer.get(position);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        fragment.setupFragment(bitmap, pageClickListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return renderer != null ? renderer.getPageCount() : 0;
    }
}
