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
package es.voghdev.pdfviewpager.library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.voghdev.pdfviewpager.library.subscaleview.ImageSource;
import es.voghdev.pdfviewpager.library.subscaleview.SubsamplingScaleImageView;
import es.voghdev.pdfviewpager.library.util.EmptyClickListener;

public class PageFragment extends Fragment {
    ViewGroup viewGroup = null;
    Bitmap bitmap = null;
    int backgroundColor;
    View.OnClickListener pageClickListener = new EmptyClickListener();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.view_pdf_page, container, true);
        viewGroup.setBackgroundColor(backgroundColor);
        if (bitmap != null) {
            ((ImageView) viewGroup.findViewById(R.id.imageView)).setImageBitmap(bitmap);
            SubsamplingScaleImageView scaleImageView = viewGroup.findViewById(R.id.subsamplingImageView);
            scaleImageView.setImage(ImageSource.bitmap(bitmap));
            scaleImageView.setOnClickListener(v -> pageClickListener.onClick(v));
        }
        return viewGroup;
    }

    public void setupFragment(Bitmap bitmap, View.OnClickListener clickListener, int backgroundColor) {
        this.bitmap = bitmap;
        this.backgroundColor = backgroundColor;
        pageClickListener = clickListener;
    }
}
