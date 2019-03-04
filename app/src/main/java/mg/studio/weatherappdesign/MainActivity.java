package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadUpdate().execute();
        init();
    }

    public void init(){
        Date date=new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String a=dateFm.format(date);
        ((TextView)findViewById(R.id.Weekend)).setText(a);
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();

    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://apis.juhe.cn/simpleWeather/query?" +
                    "city=%E9%87%8D%E5%BA%86&key=ea21c3c747bf2b853410b7c245e8f23c";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                   // Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.d("file:","this is the json");
                Log.d("file:",buffer.toString());
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                Toast.makeText(getApplicationContext(), "天气更新失败", Toast.LENGTH_SHORT).show();
            } catch (ProtocolException e) {
                Toast.makeText(getApplicationContext(), "天气更新失败", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "天气更新失败", Toast.LENGTH_SHORT).show();
            }

            return null;
        }



        private String[][] parseJSONWithJSONObject(String jsonData){
            try{
                String[][] back = new String[6][3];
                String date;
                String fTemperature,fWeather;
                JSONObject jsonObject = JSON.parseObject(jsonData);
                String result = jsonObject.getString("result");
                JSONObject resultObject = JSON.parseObject(result);
                String realTime = resultObject.getString("realtime");
                String city = resultObject.getString("city");
                JSONObject realTimeObject = JSON.parseObject(realTime);
                String temperature = realTimeObject.getString("temperature");
                String weather = realTimeObject.getString("info");
                Log.d("file:",temperature);
                back[0][0]=temperature;
                back[0][1]=city;
                back[0][2]=weather;
                JSONArray fArray = resultObject.getJSONArray("future");
                for(int i =0;i<fArray.size();i++){
                    JSONObject jo = fArray.getJSONObject(i);
                    date = jo.getString("date");
                    fTemperature = jo.getString("temperature");
                    fWeather = jo.getString("weather");

                    back[i+1][0]=fTemperature;
                    back[i+1][1]=date;
                    back[i+1][2]=fWeather;
                }
                return back;
            }
            catch(Exception e){e.printStackTrace();}
            return null;
        }


        private void weatherChange(String weather,int id){
            switch (weather){
                case "晴":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.sunny_small));
                    break;
                case "小雨":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.rainy_small));
                case "中雨":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.rainy_small));
                case "大雨":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.rainy_small));
                    break;
                case"阴转晴":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.partly_sunny_small));
                    break;
                case"晴转阴":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.partly_sunny_small));
                    break;
                case"阴":
                    ((ImageView)findViewById(id)).
                            setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.partly_sunny_small));
                    break;
            }

        }
        @Override
        //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        //这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
           String[][] t= parseJSONWithJSONObject(temperature);
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(t[0][0]);
            weatherChange(t[0][2],R.id.img_weather_condition);
            weatherChange(t[2][2],R.id.future_image1);
            weatherChange(t[3][2],R.id.future_image2);
            weatherChange(t[4][2],R.id.future_image3);
            weatherChange(t[5][2],R.id.future_image4);
           ((TextView) findViewById(R.id.tv_date)).setText(t[1][1]);
           ((TextView) findViewById(R.id.tv_location)).setText(t[0][1]);
            ((TextView) findViewById(R.id.future_1)).setText(t[2][1]+"  "+t[2][0]);
            ((TextView) findViewById(R.id.future_2)).setText(t[3][1]+"  "+t[3][0]);
            ((TextView) findViewById(R.id.future_3)).setText(t[4][1]+"  "+t[4][0]);
            ((TextView) findViewById(R.id.future_4)).setText(t[5][1]+"  "+t[5][0]);
           Toast.makeText(getApplicationContext(), "天气已更新", Toast.LENGTH_SHORT).show();
        }
    }
}
