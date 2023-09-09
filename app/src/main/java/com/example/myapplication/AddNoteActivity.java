package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CheckListAdapter;
import com.example.myapplication.adapters.ImageAdapter;
import com.example.myapplication.databinding.ActivityAddNoteBinding;
import com.example.myapplication.databinding.FontSizeMenuBinding;
import com.example.myapplication.models.CheckListModel;
import com.example.myapplication.models.ForegroundColorSpanModel;
import com.example.myapplication.models.ImageModel;
import com.example.myapplication.models.SizeSpanModel;
import com.example.myapplication.models.StyleSpanModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import top.defaults.colorpicker.ColorPickerPopup;

public class AddNoteActivity extends AppCompatActivity {
    ActivityAddNoteBinding binding;
    String title, note, formatting_style, formatting_color, images, sizes;
    int id, isPinned;
    int size;
    Gson gson = new Gson();
    CheckListAdapter checkListAdapter;
    ImageAdapter imageAdapter;
    TextWatcher textWatcher;
    List<StyleSpanModel> styles = new ArrayList<>();
    List<SizeSpanModel> fontSizes = new ArrayList<>();
    List<ForegroundColorSpanModel> foregroundColors = new ArrayList<>();
    ArrayList<CheckListModel> arrayList = new ArrayList<>();
    ArrayList<ImageModel> imageList = new ArrayList<>();
    Uri imageUri;
    Bitmap bitmap;
    @SuppressLint({"RestrictedApi", "WrongThread", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        id = getIntent().getIntExtra("Id", 0);
        title = getIntent().getStringExtra("Title");
        note = getIntent().getStringExtra("Note");
        formatting_style = getIntent().getStringExtra("Formatting_style");
        formatting_color = getIntent().getStringExtra("Formatting_color");
        images = getIntent().getStringExtra("Images");
        sizes = getIntent().getStringExtra("Sizes");
        isPinned = getIntent().getIntExtra("Is_pinned", 1);
        binding.rvCheckList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvImageList.setLayoutManager(new GridLayoutManager(this, 2));
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Note");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }
        textWatcher = new TextWatcher() {
            private CharSequence beforeText;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                beforeText = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().equals(beforeText)) {
                    if (count > 0 && charSequence.charAt(start + count - 1) == '\n') {
                        int startSelection = binding.edtNote.getSelectionStart();
                        Editable text = binding.edtNote.getText();
                        String lineBeforeCursor = getLineBeforeCursor(text, start);
                        if (lineBeforeCursor.trim().startsWith("●")) {
                            String newBullet = "● ";
                            text.insert(startSelection, newBullet);
                            Log.d("TAG", "onTextChanged: something");
                        }
                    }
                    convertBullets(binding.edtNote.getText());
                    checkEditTextFields();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        binding.edtTitle.addTextChangedListener(textWatcher);
        binding.edtNote.addTextChangedListener(textWatcher);
        binding.edtNote.setClickable(true);
        if (title != null && note != null) {
            try {
                arrayList = gson.fromJson(note, new TypeToken<List<CheckListModel>>() {
                }.getType());
                checkListAdapter = new CheckListAdapter(this, arrayList, this);
                binding.rvCheckList.setAdapter(checkListAdapter);
                checkListAdapter.notifyItemRangeInserted(0, arrayList.size());
                binding.edtNote.setText("");
                binding.edtNote.setVisibility(View.GONE);
                binding.btnAddBullets.setVisibility(View.GONE);
                binding.rvCheckList.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                binding.edtNote.setText(note);
            }
            if (!binding.edtNote.getText().toString().isEmpty()) {
                SpannableStringBuilder text = new SpannableStringBuilder(binding.edtNote.getText());
                List<StyleSpanModel> styleSpans = gson.fromJson(formatting_style, new TypeToken<List<StyleSpanModel>>() {
                }.getType());
                List<ForegroundColorSpanModel> foregroundColorSpans = gson.fromJson(formatting_color,
                        new TypeToken<List<ForegroundColorSpanModel>>() {
                        }.getType());
                List<SizeSpanModel> sizeSpans = gson.fromJson(sizes, new TypeToken<List<SizeSpanModel>>() {
                }.getType());
//          Setting Style
                for (StyleSpanModel span : styleSpans) {
                    int spanStart = span.getStart();
                    int spanEnd = span.getEnd();
                    StyleSpan styleSpan = new StyleSpan(span.getStyle());
                    text.setSpan(styleSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
//          Setting size
                for (SizeSpanModel span : sizeSpans) {
                    int spanStart = span.getStart();
                    int spanEnd = span.getEnd();
                    AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan((int) span.getRelativeSize(), true);
                    text.setSpan(absoluteSizeSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
//          Setting Color
                for (ForegroundColorSpanModel span : foregroundColorSpans) {
                    int spanStart = span.getStart();
                    int spanEnd = span.getEnd();
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(span.getColor());
                    text.setSpan(foregroundColorSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                binding.edtNote.setText(text);
            }
            binding.edtTitle.setText(title);
            imageList = gson.fromJson(images, new TypeToken<List<ImageModel>>(){}.getType());
            if (!imageList.isEmpty()) {
                imageAdapter = new ImageAdapter(this, imageList, (adapterView, view, position, l) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this).setTitle("Delete Image?")
                            .setMessage("Are you sure you want to delete this image?")
                            .setIcon(R.drawable.baseline_delete_24)
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                imageList.remove(position);
                                imageAdapter.notifyItemRemoved(position);
                                if (imageList.isEmpty()) {
                                    binding.rvImageList.setVisibility(View.GONE);
                                }
                            }).setNegativeButton("No", (dialogInterface, i) -> {
                            });
                    builder.show();
                    return true;
                });
                binding.rvImageList.setAdapter(imageAdapter);
                binding.rvImageList.setVisibility(View.VISIBLE);
            }
        }
        binding.btnAdd.setOnClickListener(view -> saveData());

//        Setting ClickListener on Style Menu
        binding.textStyleMenu.optBold.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            StyleSpan[] existingSpans = text.getSpans(startSelection, endSelection, StyleSpan.class);
            boolean isAlreadyBold = false;
            for (StyleSpan span : existingSpans) {
                if (span.getStyle() == Typeface.BOLD) {
                    text.removeSpan(span);
                    isAlreadyBold = true;
                }
            }
            if (!isAlreadyBold) {
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                text.setSpan(boldSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
        });
        binding.textStyleMenu.optItalic.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            StyleSpan[] existingSpans = text.getSpans(startSelection, endSelection, StyleSpan.class);
            boolean isAlreadyItalic = false;
            for (StyleSpan span : existingSpans) {
                if (span.getStyle() == Typeface.ITALIC) {
                    text.removeSpan(span);
                    isAlreadyItalic = true;
                }
            }
            if (!isAlreadyItalic) {
                StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
                text.setSpan(italicSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
        });
        binding.textStyleMenu.optSize.setOnClickListener(view -> showFontSizeDialog());
        binding.textStyleMenu.optRed.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            ForegroundColorSpan redColorSpan = new ForegroundColorSpan(Color.RED);
            text.setSpan(redColorSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
        });
        binding.textStyleMenu.optGreen.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            ForegroundColorSpan greenColorSpan = new ForegroundColorSpan(Color.GREEN);
            text.setSpan(greenColorSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
        });
        binding.textStyleMenu.optBlue.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(Color.BLUE);
            text.setSpan(blueColorSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
        });
        binding.textStyleMenu.optCustom.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            binding.edtNote.clearComposingText();
            binding.edtNote.setSelection(endSelection, endSelection);
            SpannableString text = new SpannableString(binding.edtNote.getText());
            new ColorPickerPopup.Builder(this)
                    .initialColor(Color.RED)
                    .enableBrightness(true)
                    .enableAlpha(true)
                    .okTitle("Choose")
                    .cancelTitle("Cancel")
                    .showIndicator(true)
                    .showValue(false)
                    .build()
                    .show(view, new ColorPickerPopup.ColorPickerObserver() {
                        @Override
                        public void onColorPicked(int color) {
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                            text.setSpan(colorSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            binding.edtNote.setText(text);
                            binding.edtNote.setSelection(endSelection, endSelection);
                        }
                    });
        });

//        Bottom Menu ClickListener setting
        binding.btnShare.setOnClickListener(view -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            if (binding.edtNote.getVisibility() == View.VISIBLE)
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + binding.edtTitle.getText().toString() + "\nNote: " + binding.edtNote.getText().toString());
            else {
                StringBuilder temp = new StringBuilder();
                for (CheckListModel check :
                        arrayList) {
                    if (check.isChecked()) {
                        temp.append("[X] ").append(check.getText()).append("\n");
                    } else {
                        temp.append("[ ] ").append(check.getText()).append("\n");
                    }
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Title: " + binding.edtTitle.getText().toString() + "\nNote:\n" + temp.toString().trim());
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
        binding.btnAddChecklist.setOnClickListener(view -> {
            if (checkListAdapter == null) {
                checkListAdapter = new CheckListAdapter(this, arrayList, this);
                binding.rvCheckList.setAdapter(checkListAdapter);
            }
            String noteText = binding.edtNote.getText().toString().trim();
            binding.edtNote.setText("");
            if (binding.rvCheckList.getVisibility() == View.GONE) {
                arrayList.clear();
                checkListAdapter.notifyDataSetChanged();
                binding.edtNote.setVisibility(View.GONE);
                binding.btnAddBullets.setVisibility(View.GONE);
                arrayList.add(0, new CheckListModel(false, noteText));
                checkListAdapter.notifyItemInserted(0);
                moveFocus(0);
                if (!binding.edtTitle.getText().toString().isEmpty())
                    binding.btnAdd.setVisibility(View.VISIBLE);
            } else {
                arrayList.add(new CheckListModel(false, noteText));
                checkListAdapter.notifyItemInserted(arrayList.size() - 1);
                moveFocus(arrayList.size() - 1);
            }
            binding.rvCheckList.setVisibility(View.VISIBLE);
        });
        binding.btnAddBullets.setOnClickListener(view -> addBullets());
        binding.btnAddImage.setOnClickListener(view -> showImageDialog());
        ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.bottomLayout.getLayoutParams();
                binding.textStyleMenu.getRoot().setVisibility(View.VISIBLE);
                layoutParams.addRule(RelativeLayout.ABOVE, R.id.text_style_menu);
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.bottomLayout.getLayoutParams();
                binding.textStyleMenu.getRoot().setVisibility(View.GONE);
                layoutParams.addRule(RelativeLayout.ABOVE, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
        };
        binding.edtNote.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1001) {
            if (data != null) {
                imageUri = data.getData();
                Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();
                if (imageUri != null) {
                    if (imageAdapter == null) {
                        imageAdapter = new ImageAdapter(this, imageList, (adapterView, view, position, l) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this).setTitle("Delete Image?")
                                    .setMessage("Are you sure you want to delete this image?")
                                    .setIcon(R.drawable.baseline_delete_24)
                                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                                        imageList.remove(position);
                                        imageAdapter.notifyItemRemoved(position);
                                        if (imageList.isEmpty()) {
                                            binding.rvImageList.setVisibility(View.GONE);
                                        }
                                    }).setNegativeButton("No", (dialogInterface, i) -> {
                                    });
                            builder.show();
                            return true;
                        });
                        binding.rvImageList.setAdapter(imageAdapter);
                    }
                    imageList.add(new ImageModel(imageUri.toString()));
                    Toast.makeText(this, String.valueOf(imageList.size()), Toast.LENGTH_SHORT).show();
                    imageAdapter.notifyItemInserted(imageList.size()-1);
                    binding.rvImageList.setVisibility(View.VISIBLE);
                }
            }
        }
        if (requestCode == 1003 && resultCode == RESULT_OK) {
            assert data != null;
            bitmap = ((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"));
            if (bitmap != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
                } else {
                    imageUri = saveBitmapToFolder(this, bitmap, "IMG_" + timeStamp + ".jpg");
                }
                if (imageUri != null) {
                    if (imageAdapter == null) {
                        imageAdapter = new ImageAdapter(this, imageList, (adapterView, view, position, l) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this).setTitle("Delete Image?")
                                    .setMessage("Are you sure you want to delete this image?")
                                    .setIcon(R.drawable.baseline_delete_24)
                                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                                        imageList.remove(position);
                                        imageAdapter.notifyItemRemoved(position);
                                        if (imageList.isEmpty()) {
                                            binding.rvImageList.setVisibility(View.GONE);
                                        }
                                    }).setNegativeButton("No", (dialogInterface, i) -> {
                                    });
                            builder.show();
                            return true;
                        });
                        binding.rvImageList.setAdapter(imageAdapter);
                    }
                    imageList.add(new ImageModel(imageUri.toString()));
                    imageAdapter.notifyItemInserted(imageList.size()-1);
                    binding.rvImageList.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void checkEditTextFields() {
        if (binding.edtNote.getVisibility() == View.GONE) {
            if (!binding.edtTitle.getText().toString().trim().isEmpty()) {
                binding.btnAdd.setVisibility(View.VISIBLE);
            } else {
                binding.btnAdd.setVisibility(View.GONE);
            }
        } else {
            if (!binding.edtTitle.getText().toString().trim().isEmpty() &&
                    !binding.edtNote.getText().toString().trim().isEmpty()) {
                binding.btnAdd.setVisibility(View.VISIBLE);
            } else {
                binding.btnAdd.setVisibility(View.GONE);
            }
        }
    }

    private void addBullets() {
        int start = binding.edtNote.getSelectionStart();
        String s = "\n● ";
        binding.edtNote.getText().insert(start, s);
    }

    private void convertBullets(Editable editable) {
        String text = editable.toString();
        int start = text.indexOf("●");

        while (start != -1) {
            int startPos, endPos;
            Drawable drawable;
            startPos = start;
            endPos = startPos + 1;
            drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.baseline_radio_button_unchecked_24);
            assert drawable != null;
            drawable.setBounds(0, 0, 40, 40);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            editable.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = text.indexOf("●", endPos);
        }
    }

    private String getLineBeforeCursor(Editable text, int cursorPosition) {
        int lineStart = cursorPosition - 1;
        while (lineStart >= 0 && text.charAt(lineStart) != '\n') {
            lineStart--;
        }
        if (lineStart >= 0 || text.toString().indexOf('\n') == -1) {
            return text.subSequence(lineStart + 1, cursorPosition).toString();
        } else {
            return "";
        }
    }

    @SuppressLint({"RestrictedApi", "QueryPermissionsNeeded"})
    private void showImageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_menu_layout);
        dialog.findViewById(R.id.opt_camera).setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1002);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1003);
                }
            }
            dialog.dismiss();
        });
        dialog.findViewById(R.id.opt_gallery).setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1001);
            dialog.dismiss();
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @SuppressLint({"QueryPermissionsNeeded", "RestrictedApi"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                saveBitmapToFolder(this, bitmap, "IMG_" + timeStamp + ".jpg");
            }
        }
        if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1003);
                }
            }
        }
    }

    private void showFontSizeDialog() {
        Dialog dialog = new Dialog(this);
        FontSizeMenuBinding fontSizeMenuBinding = FontSizeMenuBinding.inflate(getLayoutInflater());
        dialog.setContentView(fontSizeMenuBinding.getRoot());
        size = 18;
        fontSizeMenuBinding.fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                size = i;
                fontSizeMenuBinding.tvFontSize.setText(String.valueOf(size));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fontSizeMenuBinding.btnConfirm.setOnClickListener(view -> {
            int startSelection = binding.edtNote.getSelectionStart();
            int endSelection = binding.edtNote.getSelectionEnd();
            SpannableString text = new SpannableString(binding.edtNote.getText());
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, true);
            text.setSpan(sizeSpan, startSelection, endSelection, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.edtNote.setText(text);
            binding.edtNote.setSelection(endSelection, endSelection);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void moveFocus(int position) {
        if (position >= 0 && !(position > arrayList.size())) {
            binding.rvCheckList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.rvCheckList.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RecyclerView.ViewHolder viewHolder = binding.rvCheckList.findViewHolderForAdapterPosition(position);
                    if (viewHolder instanceof CheckListAdapter.CheckListViewHolder) {
                        CheckListAdapter.CheckListViewHolder checkListViewHolder = (CheckListAdapter.CheckListViewHolder) viewHolder;
                        checkListViewHolder.binding.edtText.requestFocus();
                        checkListViewHolder.binding.edtText.setSelection(checkListViewHolder.binding.edtText.getText().length());
                    } else {
                        Log.d("TAG", "onGlobalLayout: ViewHolder not found");
                    }
                }
            });
        }
    }

    public void hideCheckList() {
        binding.edtNote.setVisibility(View.VISIBLE);
        binding.edtNote.requestFocus();
        binding.rvCheckList.setVisibility(View.GONE);
        binding.btnAdd.setVisibility(View.GONE);
        binding.btnAddBullets.setVisibility(View.VISIBLE);
    }
    private Uri saveBitmapToFolder(Context context, Bitmap bitmap, String fileName) {
        File folder = new File(context.getExternalFilesDir(null), "Images");
        if (!folder.exists()) {
            final boolean mkdirs = folder.mkdirs();
        }
        File file = new File(folder, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return FileProvider.getUriForFile(context, "com.example.myapplication.fileprovider", file);
    }
    private void saveData() {
        StyleSpan[] styleSpans = binding.edtNote.getText().getSpans(0, binding.edtNote.length() - 1, StyleSpan.class);
        AbsoluteSizeSpan[] absoluteSizeSpans = binding.edtNote.getText().getSpans(0, binding.edtNote.length(), AbsoluteSizeSpan.class);
        ForegroundColorSpan[] foregroundColorSpans = binding.edtNote.getText().getSpans(0, binding.edtNote.length() - 1,
                ForegroundColorSpan.class);
        for (StyleSpan styleSpan : styleSpans) {
            int start = binding.edtNote.getText().getSpanStart(styleSpan);
            int end = binding.edtNote.getText().getSpanEnd(styleSpan);
            int style = styleSpan.getStyle();
            styles.add(new StyleSpanModel(style, start, end));
        }
        for (AbsoluteSizeSpan absoluteSizeSpan : absoluteSizeSpans) {
            int start = binding.edtNote.getText().getSpanStart(absoluteSizeSpan);
            int end = binding.edtNote.getText().getSpanEnd(absoluteSizeSpan);
            int fontSize = absoluteSizeSpan.getSize();
            fontSizes.add(new SizeSpanModel(fontSize, start, end));
        }
        for (ForegroundColorSpan colorSpan : foregroundColorSpans) {
            int start = binding.edtNote.getText().getSpanStart(colorSpan);
            int end = binding.edtNote.getText().getSpanEnd(colorSpan);
            int color = colorSpan.getForegroundColor();
            foregroundColors.add(new ForegroundColorSpanModel(color, start, end));
        }
        Intent intent = new Intent();
        if (title != null && note != null) intent.putExtra("Id", id);
        intent.putExtra("Title", binding.edtTitle.getText().toString().trim());
        if (binding.rvCheckList.getVisibility() == View.VISIBLE) {
            intent.putExtra("Note", gson.toJson(arrayList));
        } else {
            intent.putExtra("Note", binding.edtNote.getText().toString());
        }
        intent.putExtra("Formatting_style", gson.toJson(styles));
        intent.putExtra("Formatting_color", gson.toJson(foregroundColors));
        intent.putExtra("Sizes", gson.toJson(fontSizes));
        intent.putExtra("Images", gson.toJson(imageList));
        intent.putExtra("Is_pinned", isPinned);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        saveData();
    }
}