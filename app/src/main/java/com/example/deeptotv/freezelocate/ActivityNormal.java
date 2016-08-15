package com.example.deeptotv.freezelocate;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ActivityNormal extends AppCompatActivity implements LocationListener{
    Button get_data_button,send_database_button,exit_button;
    EditText get_code_edittext,latTxt_edittext,longTxt_edittext,asset_serial_edittext;
    EditText retailer_MobileNo_edittext,outletName_edittext,dealerCode_edittext,dealerName_edittext;
    EditText seqNo_edittext,assetNo_edittext,proprieterName_edittext,prevAddress_edittext;
    EditText district_edittext,thana_edittext,village_edittext,houseNo_edittext,roadNo_edittext,documentNo_edittext;
    Spinner type_code_spinner,product_type_spinner,freeze_brand_spinner,freeze_capacity_spinner,asset_date_spinner;
    CheckBox addressOk_checkbox;

    GPSTracker gps;
    Connection conn;
    String barCode,latTextView,longTextView;
    String typeCode,productCode,freezeBrand,freezeCapacity,assetDate;
    String assetSerial,dealerCode="",dealerName="",outletName="",retailerAddress,retailerMobileNo="";
    String seqNo,assetNo,deviceId,proprieterName;
    String prevAddress,district,thana,houseNo,roadNo,villageName,combined_address="",documentNo;
    String currentDateandTime, isAddressOk;
    ResultSet rs;
    Map map_Code_name = new HashMap();
    //=================================
    protected LocationManager locationManager;
    Location location;
    //=================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_normal);
        get_data_button = (Button) findViewById(R.id.get_button);
        send_database_button = (Button) findViewById(R.id.send_button);
        exit_button = (Button) findViewById(R.id.exit_button);
        get_code_edittext = (EditText) findViewById(R.id.edittext_barcode);
        latTxt_edittext = (EditText) findViewById(R.id.loc_lat);
        longTxt_edittext = (EditText) findViewById(R.id.loc_long);
        asset_serial_edittext = (EditText) findViewById(R.id.asset_serial);
        retailer_MobileNo_edittext = (EditText) findViewById(R.id.retailer_mobile_no);
        outletName_edittext = (EditText) findViewById(R.id.outlet_name);
        proprieterName_edittext = (EditText) findViewById(R.id.proprieter_name);
        dealerCode_edittext = (EditText) findViewById(R.id.dealer_code);
        dealerName_edittext = (EditText) findViewById(R.id.dealer_name);
        type_code_spinner = (Spinner) findViewById(R.id.type_code);
        product_type_spinner = (Spinner) findViewById(R.id.product_type_spinner);
        freeze_brand_spinner = (Spinner) findViewById(R.id.freeze_brand);
        freeze_capacity_spinner = (Spinner) findViewById(R.id.freeze_capacity);
        asset_date_spinner = (Spinner) findViewById(R.id.asset_date);
        seqNo_edittext = (EditText) findViewById(R.id.seqno_edittext);
        assetNo_edittext = (EditText) findViewById(R.id.asset_code);
        prevAddress_edittext = (EditText) findViewById(R.id.prevaddress_edittext);
        district_edittext = (EditText) findViewById(R.id.district_edittext);
        thana_edittext = (EditText) findViewById(R.id.thana_edittext);
        village_edittext = (EditText) findViewById(R.id.village_edittext);
        houseNo_edittext = (EditText) findViewById(R.id.houseNo_edittext);
        roadNo_edittext = (EditText) findViewById(R.id.roadNo_edittext);
        documentNo_edittext = (EditText) findViewById(R.id.documentNo_edittext);
        addressOk_checkbox = (CheckBox) findViewById(R.id.chkAddressOk);
        //===================================================
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //onLocationChanged(location);
        //==================================================
        class GetDealerInfo implements Runnable
        {
            @Override
            public void run() {

                getDealerInfo();
            }
        }
        Thread dealerInfoThread = new Thread(new GetDealerInfo());
        //t.setDaemon(true);
        dealerInfoThread.start();
        /*============================================================================
        if(TextUtils.isEmpty(get_code_edittext.getText().toString())) {
            Toast.makeText(this, "plz enter your name ", Toast.LENGTH_SHORT).show();
            return;
        }
        ==============================================================================*/
        assetNo_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String buildAssetCode;
                String serial_no;
                if (hasFocus) {
                        serial_no = asset_serial_edittext.getText().toString();
                        if(TextUtils.isEmpty(serial_no))
                        {
                        asset_serial_edittext.setError("asset serial empty");
                        }
                        else if((serial_no.indexOf('Z',1) > 0) || (serial_no.indexOf('B',1) > 0))
                        {
                            asset_serial_edittext.setError("please input Z or B only at first position");
                        }
                        else {
                            buildAssetCode = "KFIL/" + type_code_spinner.getSelectedItem().toString() + "/";
                            buildAssetCode = buildAssetCode + product_type_spinner.getSelectedItem().toString() + "/";
                            buildAssetCode = buildAssetCode + freeze_brand_spinner.getSelectedItem().toString() + "-";
                            buildAssetCode = buildAssetCode + freeze_capacity_spinner.getSelectedItem().toString() + "L/";
                            buildAssetCode = buildAssetCode + asset_date_spinner.getSelectedItem().toString() + "/";
                            buildAssetCode = buildAssetCode + asset_serial_edittext.getText();

                            assetNo_edittext.setText(buildAssetCode);
                            assetNo_edittext.setEnabled(false);
                            //get IMEI no ========================
                            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                            deviceId = tm.getDeviceId();
                            //====================================
                            //retailer_MobileNo_edittext.setText(deviceId);

                        }

                }
            }

        });
        dealerCode_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    seqNo = seqNo_edittext.getText().toString();
                    //===================== get and set all dealer retailer info ===============

                    class GetRetailerInfoThread implements Runnable
                    {
                        @Override
                        public void run() {

                            getRetailerInfo();
                        }
                    }
                    Thread getRetailerInfoThreadObj = new Thread(new GetRetailerInfoThread());
                    getRetailerInfoThreadObj.start();
                    //==========================================================================
                }
            }

        });
        dealerName_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    setDealerName(dealerCode_edittext.getText().toString());
                }
            }

        });

        get_data_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               /*
                // create class object
                gps = new GPSTracker(ActivityNormal.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    latTxt_edittext.setText(String.valueOf(latitude));
                    longTxt_edittext.setText(String.valueOf(longitude));

                } else {
                    gps.showSettingsAlert();
                }
               */
                onLocationChanged(location);
            }
        });
    }


    public void onClickSendButton(View view)
    {

        barCode = get_code_edittext.getText().toString();
        latTextView = latTxt_edittext.getText().toString();
        longTextView = longTxt_edittext.getText().toString();
        typeCode = type_code_spinner.getSelectedItem().toString();
        productCode = product_type_spinner.getSelectedItem().toString();
        freezeBrand = freeze_brand_spinner.getSelectedItem().toString();
        freezeCapacity = freeze_capacity_spinner.getSelectedItem().toString();
        assetDate = asset_date_spinner.getSelectedItem().toString();
        assetSerial = asset_serial_edittext.getText().toString();
        dealerCode = dealerCode_edittext.getText().toString();
        outletName = outletName_edittext.getText().toString();
        proprieterName = proprieterName_edittext.getText().toString();
        district = district_edittext.getText().toString();
        thana = thana_edittext.getText().toString();
        villageName = village_edittext.getText().toString();
        houseNo = houseNo_edittext.getText().toString();
        roadNo = roadNo_edittext.getText().toString();
        if(addressOk_checkbox.isChecked())
        {
            isAddressOk = "Y";
        }
        else
        {
            isAddressOk = "N";
        }
        combined_address = "H#-"+houseNo+",R#-"+roadNo+",village/block:"+villageName+",thana:"+thana+",district:"+district;
        retailerMobileNo = retailer_MobileNo_edittext.getText().toString();
        seqNo = seqNo_edittext.getText().toString();
        assetNo = assetNo_edittext.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        currentDateandTime  = sdf.format(new Date());
        documentNo = deviceId+currentDateandTime;
        // check field and validate =================================

        if(TextUtils.isEmpty(latTextView)&&TextUtils.isEmpty(longTextView)) {
            latTxt_edittext.setError("latitude empty");
            longTxt_edittext.setError("longitude empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(assetNo)) {
            assetNo_edittext.setError("asset code empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(seqNo)) {
            seqNo_edittext.setError("seq no empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(seqNo.length()<4) {
            seqNo_edittext.setError("seq no length below 4");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(dealerCode)) {
            dealerCode_edittext.setError("dealer code empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(outletName)) {
            outletName_edittext.setError("outlet name empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(proprieterName)) {
            proprieterName_edittext.setError("proprieter name empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(district)) {
            district_edittext.setError("district name empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(thana)) {
            thana_edittext.setError("thana(PS) name empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(villageName)) {
            village_edittext.setError("village name empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(houseNo)) {
            houseNo_edittext.setError("House no empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(roadNo)) {
            roadNo_edittext.setError("Road no empty");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(retailerMobileNo) || (retailerMobileNo.length()<11)) {
            retailer_MobileNo_edittext.setError("please enter mobile no correctly");
            Toast.makeText(this, "data sending failed", Toast.LENGTH_SHORT).show();
            return;
        }

        class ConnectThread implements Runnable
        {
            @Override
            public void run() {

                connect();
            }
        }
        Thread t = new Thread(new ConnectThread());
        t.start();
        /*
        System.out.println(barCode);
        System.out.println(latTextView);
        System.out.println(longTextView);
        System.out.println(typeCode);
        System.out.println(productCode);
        System.out.println(freezeBrand);
        System.out.println(freezeCapacity);
        System.out.println(assetDate);
        System.out.println(assetSerial);
        System.out.println(assetNo);
        System.out.println(seqNo);
        System.out.println(dealerCode);
        System.out.println(outletName);
        System.out.println(proprieterName);
        System.out.println(district);
        System.out.println(thana);
        System.out.println(villageName);
        System.out.println(houseNo);
        System.out.println(roadNo);
        System.out.println(retailerMobileNo);
        System.out.println(deviceId);
        System.out.println(documentNo);
        System.out.println(combined_address);
        */
    }

    public void connect()
    {

        try
        {
            Class.forName("org.postgresql.Driver");
            //conn = DriverManager.getConnection("jdbc:postgresql://172.16.9.215:5432/cwu", "adempiere", "sadadda");
            conn = DriverManager.getConnection("jdbc:postgresql://172.16.253.10:5432/adempiere", "adempiere", "adempierews");
            Statement statement1 = conn.createStatement();
            String sql = "INSERT INTO Freezer_Location VALUES (" +
                    "'"+barCode+"','"+latTextView+"','"+longTextView+"','"+typeCode+"'," +
                    "'"+freezeCapacity+"', '"+assetDate+"','"+assetSerial+"','"+dealerCode+"'," +
                    "'"+retailerMobileNo+"','"+seqNo+"','"+assetNo+"',now(),now(),'"+deviceId+"'," +
                    "'"+productCode+"','"+freezeBrand+"','"+outletName+"','"+proprieterName+"'," +
                    "'"+combined_address+"','"+documentNo+"','"+isAddressOk+"')";
            statement1.executeUpdate(sql);
            showToast("data sent successfully");
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showToast("data sending failed\nplease check data connection");

        }catch (Exception e) {
            e.printStackTrace();
            showToast("data sending failed");

        }

    }

    public void getDealerInfo()
    {



        try
        {
            Class.forName("org.postgresql.Driver");
            //conn = DriverManager.getConnection("jdbc:postgresql://172.16.9.215:5432/cwu", "adempiere", "sadadda");
            conn = DriverManager.getConnection("jdbc:postgresql://172.16.253.10:5432/adempiere", "adempiere", "adempierews");
            Statement statement1 = conn.createStatement();
            String sql_query = "Select bp.value as dealerCode,bp.name as dealerName from C_BPartner bp\n" +
                    "join C_BP_Group bpg on (bp.C_BP_Group_ID=bpg.C_BP_Group_ID) where\n" +
                    "lower(bpg.name) like '%distributor%' and bp.isActive = 'Y' order by\n" +
                    "dealerCode";
            rs = statement1.executeQuery(sql_query);
            while(rs.next())
            {
                map_Code_name.put(rs.getString("dealerCode"),rs.getString("dealerName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showToast("database connection failed");

        }catch (Exception e) {
            e.printStackTrace();
            showToast("fetching data failed");

        }
    }
    public void getRetailerInfo()
    {

        String d_code="",d_name="",outName= "",com_address="",rMobileNo = "";

        try
        {
            Class.forName("org.postgresql.Driver");
            //conn = DriverManager.getConnection("jdbc:postgresql://172.16.9.215:5432/cwu", "adempiere", "sadadda");
            conn = DriverManager.getConnection("jdbc:postgresql://172.16.253.10:5432/adempiere", "adempiere", "adempierews");
            Statement statement1 = conn.createStatement();
            String sql_query = "Select bp.name dealer_name, bp.value dealer_code, f.outletname retailer_name, f.address, f.mobileno From t_fridgeentry f Join c_bpartner bp ON (f.c_bpartner_id = bp.c_bpartner_id) Where f.sequenceno = '"+seqNo+"'";
            rs = statement1.executeQuery(sql_query);
            while(rs.next())
            {
                d_name = rs.getString("dealer_name");
                d_code = rs.getString("dealer_code");
                outName = rs.getString("retailer_name");
                com_address = rs.getString("address");
                rMobileNo = rs.getString("mobileno");
            }

            setRetailerInfo(d_code,d_name,outName,com_address,rMobileNo);

        } catch (SQLException e) {
            e.printStackTrace();
            showToast("database connection failed");

        }catch (Exception e) {
            e.printStackTrace();
            showToast("fetching data failed");

        }
    }
    protected void setRetailerInfo(final String d_code,final String d_name,final String o_name,final String c_address,final String r_mobileNo ) {

        runOnUiThread(new Runnable() {

            public void run() {


                dealerCode_edittext.setText(d_code);
                dealerName_edittext.setText(d_name);
                outletName_edittext.setText(o_name);
                prevAddress_edittext.setText(c_address);
                if(r_mobileNo.length()>3)
                retailer_MobileNo_edittext.setText(r_mobileNo.substring(3,r_mobileNo.length()));

            }
        });
    }
    protected void setDealerName(final String dealerKey) {

        runOnUiThread(new Runnable() {

            public void run() {

                dealerName_edittext.setText((String)map_Code_name.get(dealerKey));

            }
        });
    }







    protected void showToast(final String result) {

        runOnUiThread(new Runnable() {

            public void run() {

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }
        });
    }
    protected void refresh() {

        runOnUiThread(new Runnable() {

            public void run() {

                get_code_edittext.setText(null);
                latTxt_edittext.setText(null);
                longTxt_edittext.setText(null);
                asset_serial_edittext.setText(null);
                assetNo_edittext.setText(null);
                dealerCode_edittext.setText(null);
                dealerName_edittext.setText(null);
                outletName_edittext.setText(null);
                proprieterName_edittext.setText(null);
                seqNo_edittext.setText(null);
                retailer_MobileNo_edittext.setText(null);
                district_edittext.setText(null);
                thana_edittext.setText(null);
                village_edittext.setText(null);
                roadNo_edittext.setText(null);
                houseNo_edittext.setText(null);

                documentNo_edittext.setText(currentDateandTime);


            }
        });
    }
    public void onClickExit(View view)
    {
        this.finish();
        System.exit(0);
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location.getAccuracy() < 2.0){
            //Do something
            locationManager.removeUpdates(ActivityNormal.this);
        }
        else{
            //Continue listening for a more accurate location
            setLat(location);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    protected void setLat(final Location location) {

        runOnUiThread(new Runnable() {
            public void run() {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latTxt_edittext.setText(String.valueOf(latitude));
                longTxt_edittext.setText(String.valueOf(longitude));
            }
        });
    }
}
