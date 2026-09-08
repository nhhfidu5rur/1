package com.excelmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout columnsContainer;
    private ExcelManager excelManager;
    private ArrayList<String> columns = new ArrayList<>();
    private HashMap<String, String> todayData = new HashMap<>();
    private SharedPreferences prefs;
    private static final String PREF_NAME = "ExcelManagerPrefs";
    private static final String PREF_COLUMNS = "columns";
    private static final String PREF_DATA_PREFIX = "data_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        columnsContainer = findViewById(R.id.columnsContainer);
        Button addColumnBtn = findViewById(R.id.addColumnBtn);
        Button addDataBtn = findViewById(R.id.addDataBtn);
        Button exportBtn = findViewById(R.id.exportBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        excelManager = new ExcelManager(this);

        // تحميل الأعمدة المحفوظة
        loadColumns();

        addColumnBtn.setOnClickListener(v -> showAddColumnDialog());
        addDataBtn.setOnClickListener(v -> showAddDataDialog());
        exportBtn.setOnClickListener(v -> exportToExcel());
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void loadColumns() {
        String saved = prefs.getString(PREF_COLUMNS, "");
        if (!saved.isEmpty()) {
            String[] cols = saved.split(",");
            for (String col : cols) {
                if (!col.trim().isEmpty()) {
                    columns.add(col.trim());
                    addColumnButton(col.trim());
                }
            }
        }
    }

    private void saveColumns() {
        String data = String.join(",", columns);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_COLUMNS, data);
        editor.apply();
    }

    private void showAddColumnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة قائمة جديدة");

        EditText input = new EditText(this);
        input.setHint("مثال: سحبات، الإيرادات، المصاريف");
        input.setPadding(20, 20, 20, 20);
        builder.setView(input);

        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String columnName = input.getText().toString().trim();
            if (!columnName.isEmpty() && !columns.contains(columnName)) {
                columns.add(columnName);
                addColumnButton(columnName);
                saveColumns();
                Toast.makeText(MainActivity.this, "تمت إضافة: " + columnName, Toast.LENGTH_SHORT).show();
            } else if (columns.contains(columnName)) {
                Toast.makeText(MainActivity.this, "هذه القائمة موجودة بالفعل!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void addColumnButton(String columnName) {
        Button btn = new Button(this);
        btn.setText(columnName);
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn.setTextColor(getResources().getColor(android.R.color.white));
        btn.setTextSize(14);
        btn.setPadding(10, 10, 10, 10);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        btn.setLayoutParams(params);

        columnsContainer.addView(btn);
    }

    private void showAddDataDialog() {
        if (columns.isEmpty()) {
            Toast.makeText(this, "أضف قوائم أولاً!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إدخال البيانات لليوم");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        HashMap<String, EditText> inputs = new HashMap<>();

        for (String column : columns) {
            android.widget.TextView label = new android.widget.TextView(this);
            label.setText(column);
            label.setTextSize(16);
            label.setTextStyle(android.graphics.Typeface.BOLD);
            label.setPadding(0, 15, 0, 8);
            label.setTextColor(getResources().getColor(R.color.colorPrimary));
            layout.addView(label);

            EditText input = new EditText(this);
            input.setHint("أدخل القيمة");
            input.setPadding(15, 10, 15, 10);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.Input
