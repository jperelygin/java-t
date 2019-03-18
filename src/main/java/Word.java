import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Word {

    private String enteredWord;
    private String translation;
    private String neededLanguage;
    private String APIkey;

    Word(String newWord){
        getKey();
        this.neededLanguage = "ru";
        this.enteredWord = newWord;
        this.translation = "";
    }

    Word(String neededLanguage, String newWord){
        getKey();
        this.translation = "";
        this.enteredWord = newWord;
        if (!neededLanguage.equals("ru") && !neededLanguage.equals("en")){
            this.translation = "Translation on such language is not specified";
        } else {
            if (neededLanguage.equals("ru")){
                this.neededLanguage = "ru";
            }
            if (neededLanguage.equals("en")){
                this.neededLanguage = "en";
            }
        }
    }

    private void getKey(){
        StringBuilder sb = new StringBuilder("");

        String key = "apikey.txt";
        ClassLoader cl = getClass().getClassLoader();
        File file = new File(cl.getResource(key).getFile());

        try {
            Scanner sc = new Scanner(file);
            sb.append(sc.nextLine());
            sc.close();
        } catch (Exception e){
            System.out.println(e);
        }

        this.APIkey = sb.toString();
    }

    private String osCheck(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            os = "win";
        } else if (os.contains("mac")){
            os = "mac";
        } else {
            os = "nix";
        }
        return os;
    }

    private void translate(){
        HttpURLConnection connection = null;
        try {
            String key = this.APIkey;
            String targetURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + key;
            URL url = new URL(targetURL);

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes("text=" + URLEncoder.encode(this.enteredWord, "UTF-8") + "&lang=" + this.neededLanguage);

            //listening for response
            InputStream resource = connection.getInputStream();
            String json = new Scanner(resource).nextLine();

            //result in response represents as a list
            int start = json.indexOf("[");
            int end = json.indexOf("]");

            String os = osCheck();

            //os check for different charsets
            if (os.equals("win")){
                this.translation = new String(json.substring(start+2, end-1).getBytes("windows-1251"), Charset.forName("UTF-8"));
            } else if (os.equals("mac") || os.equals("nix")){
                this.translation = new String(json.substring(start+2, end-1).getBytes(), Charset.forName("UTF-8"));
            } else {
                this.translation = this.enteredWord;
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public String getTranslation(){
        translate();
        return this.translation;
    }

    public String getEnteredWord(){
        return this.enteredWord;
    }

    public String getNeededLanguage(){
        return this.neededLanguage;
    }
        
    public String connectSQL(){
        String os = osCheck();

        Connection con = null;
        String dbUrl;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:./t.db");

            //del after tests.
            System.out.println("Connected to db");

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (con != null){
                    con.close();
                    }
            } catch (SQLException e){
                System.out.println(e.getSQLState());
            }
        }
        return "Done";
    }
}
