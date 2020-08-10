package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by NG1 on 2017-08-08.
 */

public class JsonParser {
    String jsonStaffData, jsonGalleryData, jsonGalleryWholeData, jsonNoticeData, jsonNoticeWholeData, jsonBuildingData, jsonTourData, jsonWholeData, jsonBgData, jsonVideoData, jsonAboutData, jsonVersionData, jsonAlertData, jsonFoodMenuData, jsonFoodOriginData, jsonFoodMSGData;
    String teamCode;
    final String jsonStaffFile = "KIOSK_STAFF";
    final String jsonGalleryFile = "KIOSK_GALLERY";
    final String jsonGalleryWholeFile = "KIOSK_GALLERY_WHOLE";
    final String jsonNoticeFile = "KIOSK_NOTICE";
    final String jsonNoticeWholeFile = "KIOSK_NOTICE_WHOLE";
    final String jsonBuildingFile = "KIOSK_BUILDING";
    final String jsonTourFile = "KIOSK_TOUR";
    final String jsonWholeFile = "KIOSK_WHOLE";
    final String jsonBgFile = "KIOSK_BG";
    final String jsonVideoFile = "KIOSK_VIDEO";
    final String jsonVersionFile = "KIOSK_VERSION";
    final String jsonAboutFile = "KIOSK_ABOUT";
    final String jsonAlertFile = "KIOSK_ALERT";
    final String jsonFoodOriginFile = "KIOSK_FOOD_ORIGIN";
    final String jsonFoodMSGFile = "KIOSK_FOOD_MSG";
    final String jsonFoodMenuFile = "KIOSK_FOOD_MENU";


    Bitmap imgTmp;


    protected Context context;

    public JsonParser(String teamCode, Context context) {
        this.teamCode = teamCode;
        this.context = context;

        //jsonVersionData = loadVersionData();
        loadJsonData();
    }

    JSONObject staffObject;
    JSONArray teams;
    JSONObject[] getTeams;
    JSONObject[][] getMates;
    //HashMap<String, String> section;

    public boolean parseStaffJson(String teamCode) {
        StringBuffer data = new StringBuffer();
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/" + teamCode));

            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonStaffData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("teamcode?", " " + teamCode);
            //Toast.makeText(context, "해당 과에 대한 정보가 없습니다. 나중에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            staffObject = new JSONObject(jsonStaffData);
            teams = staffObject.getJSONArray("teams");
            Log.d("jsonStaffData", jsonStaffData);
            getTeams = new JSONObject[teams.length()];
            getMates = new JSONObject[teams.length()][100];

            for (int i = 0; i < getTeams.length; i++) {
                getTeams[i] = teams.getJSONObject(i);
                for (int j = 0; j < getTeams[i].getJSONObject("mate").length(); j++) {
                    getMates[i][j] = getTeams[i].getJSONObject("mate").getJSONObject(String.valueOf(j));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    JSONArray galleryArray;
    JSONObject[] pics;

    public void parseGalleryJson() {
        try {
            galleryArray = new JSONArray(jsonGalleryData);
            pics = new JSONObject[galleryArray.length()];

            for (int i = 0; i < galleryArray.length(); i++) {
                pics[i] = galleryArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONArray galleryWholeArray;
    JSONObject[] picsWhole;

    public void parseGalleryWholeJson() {
        try {
            galleryWholeArray = new JSONArray(jsonGalleryWholeData);
            picsWhole = new JSONObject[galleryWholeArray.length()];

            for (int i = 0; i < galleryWholeArray.length(); i++) {
                picsWhole[i] = galleryWholeArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subGalleryJson() {
        List<JSONObject> objectList = new CopyOnWriteArrayList<>();
        for (JSONObject pic : pics) {
            objectList.add(pic);
        }
        for (JSONObject pic : pics) {
            for (JSONObject picWhole : picsWhole) {
                try {
                    if (pic.getString("img_no").equals(picWhole.getString("img_no"))) {
                        objectList.remove(pic);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        pics = objectList.toArray(new JSONObject[objectList.size()]);
    }

    public String getGalleryPicPath(int index) {
        try {
            String fileName = pics[index].getString("img_filenm");
            if (fileName.contains("http://") || fileName.contains("https://")) {
                if (!fileName.contains("=")) {
                    String indexFile[] = null;
                    indexFile = fileName.split("/");
                    fileName = indexFile[indexFile.length - 1] + ".png";
                    Log.d("fileName = ", fileName);
                } else {
                    int indexFile = 0;
                    indexFile = fileName.indexOf("=");
                    fileName = fileName.substring(indexFile + 1) + ".png";
                    Log.d("fileName = ", fileName);
                }
            }

            return fileName;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    JSONArray tourArray;
    JSONObject[] tourPics;

    public void parseTourJson() {
        try {
            tourArray = new JSONArray(jsonTourData);
            tourPics = new JSONObject[tourArray.length()];

            for (int i = 0; i < tourArray.length(); i++) {
                tourPics[i] = tourArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTourPicPath(int index) {
        try {
            String fileName = tourPics[index].getString("tour_image");
            if (fileName.contains("http://") || fileName.contains("https://")) {
                if (!fileName.contains("=")) {
                    String indexFile[] = null;
                    indexFile = fileName.split("/");
                    fileName = indexFile[indexFile.length - 1] + ".png";
                    Log.d("fileName = ", fileName);
                } else {
                    int indexFile = 0;
                    indexFile = fileName.indexOf("=");
                    fileName = fileName.substring(indexFile + 1) + ".png";
                    Log.d("fileName = ", fileName);
                }
            }

            return fileName;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    JSONArray aboutArray;
    JSONObject[] aboutPics;

    public void parseAboutJson() {
        try {
            aboutArray = new JSONArray(jsonAboutData);
            aboutPics = new JSONObject[aboutArray.length()];

            for (int i = 0; i < aboutArray.length(); i++) {
                aboutPics[i] = aboutArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAboutPicPath(int index) {
        try {
            return aboutPics[index].getString("in_image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    JSONArray alertArray;
    JSONObject[] alertContents;

    public void parseAlertJson() {
        try {
            alertArray = new JSONArray(jsonAlertData);
            alertContents = new JSONObject[alertArray.length()];

            for (int i = 0; i < alertArray.length(); i++) {
                alertContents[i] = alertArray.getJSONObject(i);
                Log.d("alertParsingTag", "parsing Result" + alertContents[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getAlertDate(int i) {
        int[] dateList = {0, 0, 0, 0, 0, 0};
        try {
            String date = alertContents[i].getString("regdate");
            StringTokenizer stringTokenizer = new StringTokenizer(date, "-");

            for (int j = 0; j < 2; j++) {
                dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
            }

            String temp = stringTokenizer.nextToken();
            dateList[2] = Integer.parseInt(temp.substring(0, 2));

            stringTokenizer = new StringTokenizer(temp.substring(3), ":");
            for (int j = 3; j < 5; j++) {
                dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
            }
            temp = stringTokenizer.nextToken();
            dateList[5] = Integer.parseInt(temp.substring(0, 2));
            return dateList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    public int[] getAlertStartDate(int i) {
        int[] dateList = {};
        String date = null;
        try {
            date = alertContents[i].getString("stdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
        for (int j = 0; j < 3; j++) {
            dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
        }
        return dateList;
    }

    public int[] getAlertEndDate(int i) {
        int[] dateList = {};
        String date = null;
        try {
            date = alertContents[i].getString("eddate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
        for (int j = 0; j < 3; j++) {
            dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
        }
        return dateList;
    }

    public int getAlertCount() {
        try {
            Log.d("alertParsingTag", "getLength" + alertArray.length());
            return alertArray.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getAlertContent(int i) {
        try {
            Log.d("alertParsingTag", "getLength" + alertContents[i].getString("content"));
            return alertContents[i].getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int[] getAlertLunchTime(int i) {
        int[] dateList = {0, 0, 0, 0, 0, 0};
        try {
            String date = alertContents[i].getString("sttime");
            if (date.equals(""))
                return dateList;
            StringTokenizer stringTokenizer = new StringTokenizer(date, ":");

            for (int j = 0; j < 3; j++) {
                dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
            }

            date = alertContents[i].getString("edtime");
            stringTokenizer = new StringTokenizer(date, ":");

            for (int j = 3; j < 6; j++) {
                dateList[j] = Integer.parseInt(stringTokenizer.nextToken());
            }
            return dateList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    public String[] getAlertLiveTime(int i) {
        String[] dateList = {"", "", "", "", "", ""};
        try {
            String date = alertContents[i].getString("stdate");
            StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
            if (date.equals(""))
                return dateList;
            for (int j = 0; j < 3; j++) {
                dateList[j] = stringTokenizer.nextToken();
            }

            date = alertContents[i].getString("eddate");
            stringTokenizer = new StringTokenizer(date, "-");

            for (int j = 3; j < 6; j++) {
                dateList[j] = stringTokenizer.nextToken();
            }
            return dateList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    public String getAlertCategory(int i) {
        try {
            return alertContents[i].getString("category");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getAlertType(int i) {
        try {
            if (alertContents[i].getString("type").equals("WARNING")) {
                return 1;
            }
            if (alertContents[i].getString("type").equals("DANGER")) {
                return 2;
            } else
                return 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getAlertVisible(int i) {
        try {
            Log.d("alertParsingTag", "getLength" + alertContents[i].getString("visible"));
            return alertContents[i].getString("visible");
        } catch (Exception e) {
            Log.d("MainActivity", "Alert is Null");
        }
        return null;
    }

    JSONArray noticeArray;
    JSONObject[] articles;

    public void parseNoticeJson() {
        try {
            noticeArray = new JSONArray(jsonNoticeData);
            articles = new JSONObject[noticeArray.length()];

            for (int i = 0; i < noticeArray.length(); i++) {
                articles[i] = noticeArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONArray noticeWholeArray;
    JSONObject[] articlesWhole;

    public void parseNoticeWholeJson() {
        try {
            noticeWholeArray = new JSONArray(jsonNoticeWholeData);
            articlesWhole = new JSONObject[noticeWholeArray.length()];

            for (int i = 0; i < noticeWholeArray.length(); i++) {
                articlesWhole[i] = noticeWholeArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subNoticeJson() {
        List<JSONObject> objectList = new CopyOnWriteArrayList<>();
        for (JSONObject pic : articles) {
            objectList.add(pic);
        }
        for (JSONObject pic : articles) {
            for (JSONObject picWhole : articlesWhole) {
                try {
                    Log.d(TAG, "subNoticeJson: removed: " + pic);
                    Log.d(TAG, "subNoticeJson: removed: " + picWhole);
                    Log.d(TAG, "subNoticeJson: bbsnopic: " + pic.getInt("bbs_no"));
                    Log.d(TAG, "subNoticeJson: bbsnopic: " + picWhole.getInt("bbs_no"));
                    if (pic.getInt("bbs_no") == (picWhole.getInt("bbs_no"))) {
                        objectList.remove(pic);
                    }
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        articles = objectList.toArray(new JSONObject[objectList.size()]);
    }

    JSONObject buildingObject;
    List<JSONArray> bArray, bViewerArray;
    String[][] buildingPics;
    HashMap<JSONArray, String> buildingName, buildingViewerName;

    public void parseBuildingJson() {
        boolean checked = false;
        try {
            bArray = new ArrayList<>();
            bViewerArray = new ArrayList<>();
            buildingObject = new JSONObject(jsonBuildingData);

            buildingName = new HashMap<>();
            buildingViewerName = new HashMap<>();
            for (int i = 0; i < buildingObject.length(); i++) {
                bArray.add(buildingObject.getJSONArray(buildingObject.names().getString(i)));
                buildingName.put(bArray.get(i), buildingObject.names().getString(i));
                try {
                    Log.d("buildingCheckedPut", buildingObject.names().getString(i) + "building json");
                    if (MainActivity.buildingChecked.get(buildingObject.names().getString(i) + "building")) {
                        bViewerArray.add(buildingObject.getJSONArray(buildingObject.names().getString(i)));
                        buildingViewerName.put(bArray.get(i), buildingObject.names().getString(i));
                        Log.d("BuildingChecking3 ", bArray.get(i) + " " + buildingObject.names().getString(i) + " " + bViewerArray.size());
                        checked = true;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            if (checked == false) {
                for (int i = 0; i < buildingObject.length(); i++) {
                    bViewerArray.add(buildingObject.getJSONArray(buildingObject.names().getString(i)));
                    buildingViewerName.put(bArray.get(i), buildingObject.names().getString(i));
                }
                Log.d("BuildingChecking", "checked = false");
            }

            Collections.sort(bViewerArray, new Comparator<JSONArray>() {
                @Override
                public int compare(JSONArray a, JSONArray b) {
                    int valA = 0;
                    int valB = 0;

                    try {
                        valA = a.getJSONObject(0).getInt("bu_type");
                        valB = b.getJSONObject(0).getInt("bu_type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return valB - valA;
                }
            });


            buildingPics = new String[bViewerArray.size()][];
            for (int i = 0; i < bViewerArray.size(); i++) {
                buildingPics[i] = new String[bViewerArray.get(i).length()];
                for (int j = 0; j < bViewerArray.get(i).length(); j++)
                    buildingPics[i][j] = bViewerArray.get(i).getJSONObject(j).getString("bu_img");
            }
            Log.d("bViewerArraySize", String.valueOf(bViewerArray.size()));
        } catch (Exception e) {
            Log.d("buildingcatch", "catch");
            e.printStackTrace();
        }
    }


    JSONObject wholeObject;
    JSONArray[] wholeTeams;
    JSONObject[][] wholeGetTeams;

    public void parseWholeStaffJson() {
        try {
            //section.clear();
            wholeObject = new JSONObject(jsonWholeData);
            wholeTeams = new JSONArray[wholeObject.length()];
            wholeGetTeams = new JSONObject[wholeTeams.length][];
            for (int i = 0; i < wholeObject.length(); i++) {
                wholeTeams[i] = wholeObject.getJSONArray(wholeObject.names().getString(i));
                wholeGetTeams[i] = new JSONObject[wholeTeams[i].length()];
                //Log.d("WholeLog", wholeTeams[i] + "\n" + wholeGetTeams[i]);
                for (int j = 0; j < wholeTeams[i].length(); j++) {
                    wholeGetTeams[i][j] = wholeTeams[i].getJSONObject(j);//mark
                    //Log.d("WholeLog2", "wholeGetTeams" + wholeGetTeams[i][j]);
                    //section.put(wholeGetTeams[i][j].getString("section_name"), wholeGetTeams[i][j].getString("section_fullcode"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    JSONArray bgArray;
    String[] bgPics;

    /*public HashMap<String, String> getSectionNameHash(){
        return section;
    }*/
    public void parseBgJson() {
        try {
            bgArray = new JSONArray(jsonBgData);
            bgPics = new String[bgArray.length()];

            for (int i = 0; i < bgArray.length(); i++) {
                imgTmp = null;
                bgPics[i] = bgArray.getJSONObject(i).getString("bi_img");
            }
        } catch (Exception e) {
            Log.d("JsonParser", "bg is null");

        }
    }

    JSONArray videoArray;
    String[] videos;
    String[] videoName;
    String[] videoTeamCode;

    public boolean parseVideoJson() {
        if (jsonVideoData == null || jsonVideoData.equals("error") || jsonVideoData.equals("[]"))
            return false;

        try {
            jsonVideoData = jsonVideoData.replace("\\", "");
            videoArray = new JSONArray(jsonVideoData);
            videos = new String[videoArray.length()];
            videoName = new String[videoArray.length()];
            videoTeamCode = new String[videoArray.length()];

            for (int i = 0; i < videoArray.length(); i++) {
                imgTmp = null;
                videos[i] = videoArray.getJSONObject(i).getString("vi_video");
                videoName[i] = videoArray.getJSONObject(i).getString("vi_name");
                videoTeamCode[i] = videoArray.getJSONObject(i).getString("section_cd");
                Log.d("videos", videos[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    public boolean subVideoJson(String teamCode) {
        if (jsonVideoData == null || jsonVideoData.equals("error") || jsonVideoData.equals("[]"))
            return false;

        try {
            jsonVideoData = jsonVideoData.replace("\\", "");
            videoArray = new JSONArray(jsonVideoData);
            Log.d(TAG, "subVideoJson: videoArray: " + videoArray.length());
            JSONArray tmpVideoArray = new JSONArray();
            for (int i = 0; i < videoArray.length(); i++) {
                JSONObject object = videoArray.getJSONObject(i);
                Log.d(TAG, "subVideoJson: " + object.getString("section_cd"));
                if (!object.getString("section_cd").equals(teamCode)) {
                    Log.d(TAG, "subVideoJson: remove: " + i);
                    tmpVideoArray.put(videoArray.getJSONObject(i));
                }
            }
            videoArray = tmpVideoArray;
            videos = new String[videoArray.length()];
            videoName = new String[videoArray.length()];
            videoTeamCode = new String[videoArray.length()];

            for (int i = 0; i < videoArray.length(); i++) {
                imgTmp = null;
                videos[i] = videoArray.getJSONObject(i).getString("vi_video");
                videoName[i] = videoArray.getJSONObject(i).getString("vi_name");
                videoTeamCode[i] = videoArray.getJSONObject(i).getString("section_cd");
                Log.d("videos", videos[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    JSONArray foodMenuArray;
    String[] foodMenus;
    String[] foodDates;

    public boolean parseFoodMenuJson() {
        if (jsonFoodMenuData == null || jsonFoodMenuData.equals("error") || jsonFoodMenuData.equals("[]"))
            return false;

        try {
            jsonFoodMenuData = jsonFoodMenuData.replace("\\", "");
            foodMenuArray = new JSONArray(jsonFoodMenuData);
            foodMenus = new String[foodMenuArray.length()];
            foodDates = new String[foodMenuArray.length()];

            for (int i = 0; i < foodMenuArray.length(); i++) {
                imgTmp = null;
                foodMenus[i] = foodMenuArray.getJSONObject(i).getString("fd_content");
                //SimpleDateFormat dt =new SimpleDateFormat("yyyy-M-d");
                foodDates[i] = foodMenuArray.getJSONObject(i).getString("fd_day");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    public String getFoodDate(int index) {
        return foodDates[index];
    }

    public String getFoodMenu(Calendar calendar) {
        try {
            String date = DateFormat.format("yyyy-M-d", calendar).toString();
            Log.d("dateChk", date);
            Log.d("datgeCheck??", String.valueOf(foodMenus));
            for (int i = 0; i < foodMenus.length; i++) {
                if (date.equals(foodDates[i]))
                    return foodMenus[i];
                Log.d("dateChkfor", foodDates[i]);
            }
        } catch (Exception e) {
            return "식단이 없습니다.";
        }
        return "식단이 없습니다.";
    }

    JSONArray foodOriginArray;
    String[] foodOrigins;

    public boolean parseFoodOriginJson() {
        if (jsonFoodOriginData == null || jsonFoodOriginData.equals("error") || jsonFoodOriginData.equals("[]"))
            return false;

        try {
            jsonFoodOriginData = jsonFoodOriginData.replace("\\", "");
            foodOriginArray = new JSONArray(jsonFoodOriginData);
            foodOrigins = new String[foodOriginArray.length()];

            for (int i = 0; i < foodOriginArray.length(); i++) {
                imgTmp = null;
                foodOrigins[i] = foodOriginArray.getJSONObject(i).getString("fd_content");
                Log.d("foodOrigins", foodOrigins[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    public String getFoodOriginContext(int index) {
        return foodOrigins[index];
    }

    JSONArray foodMSGArray;
    String[] foodMSGs;

    public boolean parseFoodMSGJson() {
        if (jsonFoodMSGData == null || jsonFoodMSGData.equals("error") || jsonFoodMSGData.equals("[]"))
            return false;

        try {
            jsonFoodMSGData = jsonFoodMSGData.replace("\\", "");
            foodMSGArray = new JSONArray(jsonFoodMSGData);
            foodMSGs = new String[foodMSGArray.length()];

            for (int i = 0; i < foodMSGArray.length(); i++) {
                imgTmp = null;
                foodMSGs[i] = foodMSGArray.getJSONObject(i).getString("fd_content");
                Log.d("foodMSGs", foodMSGs[i]);
            }
            Arrays.sort(foodMSGs);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    public String getFoodMSGContext(int index) {
        return foodMSGs[index];
    }

    public String[] getVideoList() {
        String[] emptyVideos = new String[1];
        emptyVideos[0] = "null";
        if (videos.length > 0)
            return videos;
        else
            return emptyVideos;
    }

    public String getVideoFile(int index) {
        String result;
        result = videos[index];
        return videos[index];
    }

    public String getVideoName(int index) {
        return videoName[index];
    }

    public String getVideoTeamCode(int index) {
        return videoTeamCode[index];
    }

    public int getVideoCount() {
        return videos.length;
    }

    private String[] sections, versions;
    private JSONObject versionObject;

    public boolean parseVersionJson() {
        if (jsonVersionData.equals("error") || jsonVersionData.equals("[]"))
            return false;

        try {
            versionObject = new JSONObject(jsonVersionData);
            sections = new String[versionObject.length()];
            versions = new String[versionObject.length()];
            for (int i = 0; i < versions.length; i++) {
                sections[i] = versionObject.names().getString(i);
                versions[i] = versionObject.getString(versionObject.names().getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getStaffPicPath(int team, int mate) {
        try {
            String fileName = getMates[team][mate].getString("img_filenm");

            if (fileName.contains("http://") || fileName.contains("https://")) {
                if (!fileName.contains("=")) {
                    String indexFile[] = null;
                    indexFile = fileName.split("/");
                    fileName = indexFile[indexFile.length - 1] + ".png";
                    Log.d("fileName = ", fileName);
                } else {
                    int indexFile = 0;
                    indexFile = fileName.indexOf("=");
                    fileName = fileName.substring(indexFile + 1) + ".png";
                    Log.d("fileName = ", fileName);
                }
            }

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getBgNum() {
        return bgArray.length();
    }

    public String getBgImg(int index) {
        return bgPics[index];
    }

    public String getBgName(int index) {
        try {
            return bgArray.getJSONObject(index).getString("bi_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getTeamCode() {
        try {
            return staffObject.getString("section_fullcode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    ///staff
    public String getPassword() {
        try {
            return staffObject.getString("section_pass");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getTeamCount() {
        return teams.length() - 1;
    }

    public String getTeamName(int team) {
        try {
            String name = getTeams[team].getString("team_nm");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                if (i > 0) {
                    result.append("  ");
                }

                result.append(name.charAt(i));
            }
            return result.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getMateCount(int team) {
        int tmp = 0;
        for (int i = 0; i < getMates[team].length - 1; i++)
            if (getMates[team][i] != null)
                tmp++;
        return tmp - 1;
    }

    public int getSeatPos(int team, int mate) {
        try {
            return getMates[team][mate].getInt("st_sort");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getDeptName(int team, int mate) {
        try {
            return getMates[team][mate].getString("real_use_dep_nm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getName(int team, int mate) {
        try {
            return getMates[team][mate].getString("usr_nm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDuty(int team, int mate) {
        try {
            return getMates[team][mate].getString("posit_nm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMission(int team, int mate) {
        try {
            return getMates[team][mate].getString("adi_info7");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getIsWorking(int team, int mate) {
        try {
            return getMates[team][mate].getInt("st_status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setIsWorking(int team, int mate, int isWorking) {
        try {
            getMates[team][mate].put("st_status", isWorking);

            File file = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData"), getTeamCode());
            FileWriter f = new FileWriter(file);
            f.write(staffObject.toString());
            f.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTel(int team, int mate) {
        try {
            return getMates[team][mate].getString("telno");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMail(int team, int mate) {
        try {
            return getMates[team][mate].getString("email_addr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getMemberIdentifier(int team, int mate) {
        try {
            return getMates[team][mate].getInt("st_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    ///staff end

    ///gallery
    public int getGalleryCount() {
        return pics.length;
    }

    public int getAboutCount() {
        return aboutArray.length();
    }

    public int getGalleryNum(int index) {
        try {
            return pics[index].getInt("img_no");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getGalleryTitle(int index) {
        try {
            return pics[index].getString("img_title");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "이미지 정보가 없습니다";
        }
        return null;
    }

    public String getGalleryCaption(int index) {
        try {
            return pics[index].getString("caption");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    ///gallery end


    ///tour
    public int getTourCount() {
        return tourArray.length();
    }

    public int getTourNum(int index) {
        try {
            return pics[index].getInt("img_no");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTourTitle(int index) {
        try {
            return pics[index].getString("img_title");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "이미지 정보가 없습니다";
        }
        return null;
    }

    public String getTourCaption(int index) {
        try {
            return pics[index].getString("caption");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    ///tour end

    ///notice
    public int getNoticeCount() {
        try {
            return articles.length;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public int getNoticeNum(int index) {
        try {
            return articles[index].getInt("bbs_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getNoticeTitle(int index) {
        try {
            return articles[index].getString("bbs_title");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "등록된 공지사항이 없습니다.";
        } catch (NullPointerException e) {
            return "등록된 공지사항이 없습니다.";
        }
        return null;
    }

    public String getNoticeContent(int index) {
        try {
            return articles[index].getString("bbs_content");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return "등록된 공지사항이 없습니다.";
        }
        return null;
    }

    public String getNoticePicPath(int index) {
        try {
            return articles[index].getString("bbs_file");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    ///notice end

    ///building
    public int getBuildingNum() {
        return bArray.size();
    }

    public String getBuildingName(int fIndex) {
        return buildingName.get(bArray.get(fIndex));
    }

    public int getBuildingNumViewer() {
        return bViewerArray.size();
    }

    public int getBuildingFloorCountViewer(int bIndex) {
        return bViewerArray.get(bIndex).length();
    }

    public String getBuildingNameViewer(int fIndex) {
        return buildingViewerName.get(bViewerArray.get(fIndex));
    }

    ///wholeStaff
    public String getMemberSection(int tIndex, int mIndex) {
        try {
            if (mIndex == 0)
                mIndex++;
            return wholeGetTeams[tIndex][mIndex].getString("section_fullcode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMemberTeam(int tIndex, int mIndex) {
        try {
            return wholeGetTeams[tIndex][mIndex].getString("section_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void loadJsonData() {
        StringBuffer data;
        FileInputStream fis;
        BufferedReader buffer;
        String str;

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonGalleryFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonGalleryData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonGalleryWholeFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonGalleryWholeData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonTourFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonTourData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonNoticeFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonNoticeData = data.toString();
            buffer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonNoticeWholeFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonNoticeWholeData = data.toString();
            buffer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonBuildingFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonBuildingData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        String dateString = DateFormat.format("yyyy-M", calendar).toString();
        try {
            calendar.add(Calendar.MONTH, -1);

            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonFoodMenuFile + "_" + dateString));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonFoodMenuData = data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 0);
            dateString = DateFormat.format("yyyy-M", calendar).toString();

            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonFoodMenuFile + "_" + dateString));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonFoodMenuData = jsonFoodMenuData + data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            dateString = DateFormat.format("yyyy-M", calendar).toString();

            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonFoodMenuFile + "_" + dateString));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonFoodMenuData = jsonFoodMenuData + data.toString();

            jsonFoodMenuData = jsonFoodMenuData.replaceAll("\\[", "");
            jsonFoodMenuData = jsonFoodMenuData.replaceAll("\\]", "");
            jsonFoodMenuData = jsonFoodMenuData.replaceAll("\\}\n\\{", "},{");
            jsonFoodMenuData = "[" + jsonFoodMenuData + "]";

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/KIOSKData");

            dir.mkdirs();
            File file = new File(dir, "KIOSK_FOOD_MENU_TEST" + "_" + dateString);
            FileWriter f = null;
            try {
                f = new FileWriter(file);
                f.write(jsonFoodMenuData.toString());
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonFoodOriginFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonFoodOriginData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            calendar = Calendar.getInstance();
            dateString = DateFormat.format("yyyy-M", calendar).toString();

            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonFoodMSGFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonFoodMSGData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonIconFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonIconData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonWholeFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonWholeData = data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonBgFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonBgData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonVideoFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonVideoData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonAboutFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonAboutData = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data = new StringBuffer();
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonAlertFile));
            buffer = new BufferedReader(new InputStreamReader(fis));
            str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            jsonAlertData = data.toString();
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadVersionData() {
        String tmpVersionData = null;
        try {
            StringBuffer data = new StringBuffer();
            FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", jsonVersionFile));
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            tmpVersionData = data.toString();
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpVersionData;
    }
}
