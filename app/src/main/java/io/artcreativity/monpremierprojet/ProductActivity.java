package io.artcreativity.monpremierprojet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.artcreativity.monpremierprojet.adapters.ProductAdapter;
import io.artcreativity.monpremierprojet.dao.DataBaseHelper;
import io.artcreativity.monpremierprojet.dao.DataBaseRoom;
import io.artcreativity.monpremierprojet.dao.ProductDao;
import io.artcreativity.monpremierprojet.dao.ProductRoomDao;
import io.artcreativity.monpremierprojet.databinding.ActivityProductBinding;
import io.artcreativity.monpremierprojet.entities.Product;

public class ProductActivity extends AppCompatActivity {

    private ActivityProductBinding binding;
    private List<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    final static int MAIN_CALL = 120;
    private ProductDao productDao;
    private ProductRoomDao productRoomDao;
    final static int PRODUCT_DETAIL_CALL = 122;
    private static final String TAG="ProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
//        productDao = new ProductDao(this);
//        generateProducts();
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
            startActivityIfNeeded(intent, MAIN_CALL);
        });

//        binding.ourListView.setAdapter(new ArrayAdapter<Product>(this, R.layout.simple_product_item, products.toArray(new Product[]{})));
//        buildSimpleAdapterData();

        buildCustomAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (productRoomDao == null) {
            productRoomDao = DataBaseRoom.getInstance(this).productRoomDao();
            new Thread(new Runnable() {
                final List<Product> localProducts = new ArrayList<>();

                @Override
                public void run() {
                    localProducts.addAll(productRoomDao.findAll());
                    runOnUiThread(() -> {
                        products.addAll(localProducts);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }).start();
        }
//        dataBaseHelper = new DataBaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productRoomDao == null) {
            productRoomDao = DataBaseRoom.getInstance(this).productRoomDao();
            new Thread(new Runnable() {
                final List<Product> localProducts = new ArrayList<>();

                @Override
                public void run() {
                    localProducts.addAll(productRoomDao.findAll());
                    runOnUiThread(() -> {
                        products.addAll(localProducts);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }).start();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAIN_CALL && resultCode == RESULT_OK) {
            // TODO: 18/11/21 Insertion des produits dans la listview
            assert data != null;
            if (data.hasExtra("MY_PROD")) {
                Product product = (Product) data.getExtras().getSerializable("MY_PROD");
                products.add(product);
//                products = productDao.findAll();
                buildCustomAdapter();
                Toast.makeText(getApplicationContext(), "Nouveau produit ajouter", Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == PRODUCT_DETAIL_CALL && resultCode == RESULT_OK){
            assert data != null;
            if (data.hasExtra("MY_PROD")) {
                Product product = (Product) data.getExtras().getSerializable("MY_PROD");
                products.removeIf(product1 -> product1.id == product.id);
                productAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void buildCustomAdapter() {
        productAdapter = new ProductAdapter(   this, products);
        binding.ourListView.setAdapter(productAdapter);
        binding.ourListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Product product = (Product)binding.ourListView.getItemAtPosition(position);
            Intent intent = new Intent(ProductActivity.this, ProductDetailActivity.class);
            intent.putExtra("MY_PROD", product);
            startActivityIfNeeded(intent, PRODUCT_DETAIL_CALL);
        });
    }

    private void buildSimpleAdapterData() {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Product product :
                products) {
            Map<String, String> map = new HashMap<>();
            map.put("name", product.name);
            map.put("price", "XOF " + product.price);
            map.put("quantity",  product.quantityInStock + " disponible" +
                    (product.quantityInStock>1 ? "s" : ""));
            mapList.add(map);
        }
        binding.ourListView.setAdapter(new SimpleAdapter(this, mapList, R.layout.regular_product_item,
                new String[]{"name", "quantity", "price"}, new int[]{R.id.name, R.id.quantity_in_stock, R.id.price}));
    }

    private void generateProducts() {
//        products = productDao.findAll();
//        if(products.isEmpty()) {
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productDao.findAll();
//        }

        Thread thread = new Thread(new Runnable() {
            final List<Product> localProducts = new ArrayList<>();
            @Override
            public void run() {
                localProducts.addAll(productRoomDao.findAll());
                if(localProducts.isEmpty()) {
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
                    productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
                    productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));

                    localProducts.addAll(productRoomDao.findAll());
                }
                runOnUiThread(()->{
                    products.addAll(localProducts);
                });
            }
        });
        thread.start();
//        products = productRoomDao.findAll();
//        if(products.isEmpty()) {
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy Note 10", "Samsung Galaxy Note 10", 800000, 100, 10));
//            productRoomDao.insert(new Product("Redmi S11", "Xiaomi Redmi S11", 300000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//            productRoomDao.insert(new Product("Galaxy S21", "Samsung Galaxy S21", 800000, 100, 10));
//
//            products = productRoomDao.findAll();
//        }


    }

}