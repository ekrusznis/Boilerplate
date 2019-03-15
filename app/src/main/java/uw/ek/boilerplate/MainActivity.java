package uw.ek.boilerplate;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uw.ek.boilerplate.API.ApiInterface;
import uw.ek.boilerplate.API.RetrofitApiClient;
import uw.ek.boilerplate.Adapters.HorizontalAdapter;
import uw.ek.boilerplate.Adapters.VerticalAdapter;
import uw.ek.boilerplate.Helpers.GridSpacingItemDecoration;
import uw.ek.boilerplate.Helpers.NetworkCheckingClass;
import uw.ek.boilerplate.Models.Datum;
import uw.ek.boilerplate.Models.JsonData;
import uw.ek.boilerplate.Models.Popular;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    RecyclerView recyclerViewHorizontal;
    RecyclerView recyclerViewVertical;
    HorizontalAdapter horizontalAdapter;
    VerticalAdapter verticalAdapter;
    List<Popular> popularList;
    List<Datum> dataList;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.activitymain);
        recyclerViewHorizontal = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        recyclerViewVertical = (RecyclerView) findViewById(R.id.vertical_recycler_view);
        recyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        popularList = Collections.<Popular>emptyList();
        dataList = Collections.<Datum>emptyList();
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        if (NetworkCheckingClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            fetchData();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No internet Connection", Toast.LENGTH_LONG).show();
        }


        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Toast.makeText(MainActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    case R.id.mycart:
                        Toast.makeText(MainActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                    default:
                        return true;
                }




            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {

        Call<JsonData> call = apiInterface.apiCall();
        call.enqueue(new Callback<JsonData>() {
            @Override
            public void onResponse(Call<JsonData> call, Response<JsonData> response) {

                JsonData jsonData = response.body();

                popularList = jsonData.getPopular();
                dataList = jsonData.getData();

                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

                //for spacing after every item
                if (popularList.size() > 0)
                    recyclerViewHorizontal.addItemDecoration(new GridSpacingItemDecoration(popularList.size(), spacingInPixels, true, 0));

                progressBar.setVisibility(View.GONE);

                relativeLayout.setBackgroundColor(Color.parseColor("#3481c1"));


                horizontalAdapter = new HorizontalAdapter(MainActivity.this, popularList);
                recyclerViewHorizontal.setAdapter(horizontalAdapter);
                verticalAdapter = new VerticalAdapter(MainActivity.this, dataList);
                recyclerViewVertical.setAdapter(verticalAdapter);
            }

            @Override
            public void onFailure(Call<JsonData> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}