
import org.jsoup.nodes.Document;
import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Microwave {

    public static void main(String[] args) throws IOException {
        Folder fldr = new Folder();
        fldr.createFile();
        ParseCategory parseCategory = new ParseCategory("https://bt.rozetka.com.ua/Microwave1s/c80162/filter/");
        parseCategory.parsing();
    }
}

class ParseCategory {
    private String html;

    public ParseCategory(String html) {
        this.html = html;
    }


    public void parsing() throws IOException {
        int ct = 0;
        Document doc = Jsoup.connect(this.html).get();
        Elements nums = doc.select("a.paginator-catalog-l-link");
        Element n = nums.last();
        ArrayList<Integer> pages = new ArrayList<Integer>();
        for (Element num : nums) {
            try {
                Integer text = Integer.parseInt(num.ownText());
                pages.add(text);
            } catch (NumberFormatException nfe) {
                ct++;
            }
        }
        int numOfPages = pages.get(pages.size() - 1);
        for (int pageNum = 0; pageNum < numOfPages; pageNum++) {
            String pg = html + "page=" + Integer.toString(pageNum + 1);
            parseCategoryPage(pg);
        }

    }

    public void parseCategoryPage(String pg) throws IOException {
        Document doc = Jsoup.connect(html).get();
        Elements tiles = doc.select("div.g-i-tile-i-title");
        for (Element tile : tiles) {
            Elements links = tile.select("a");
            String link = links.attr("href") + "comments/";
            parseReviews(link);
        }
    }


    public void parseReviews(String link) throws IOException {
        int ct = 0;
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ArrayList<HashMap<String, String[]>> sentiments = new ArrayList<HashMap<String, String[]>>();
        Document doc = Jsoup.connect(link).get();
        Elements nums = doc.select("a.paginator-catalog-l-link");
        for (Element num : nums) {
            try {
                String n = num.ownText();
                Integer nn = Integer.parseInt(n);
                arr.add(nn);
            } catch (NumberFormatException e) {
                ct++;
            }
        }
        int numReviews;

        if (arr.size() != 0) {
            numReviews = arr.get(arr.size() - 1);
            for (int i = 0; i < numReviews; i++) {
                String pg = link + "page=" + Integer.toString(i + 1);
                sentiments.add(parseReviewsPage(pg));
            }
        }
    }

    public HashMap<String, String[]> parseReviewsPage(String pg) throws IOException {
        Integer counter = 0;
        Document docc = Jsoup.connect(pg).get();
        Elements reviews = docc.select("article.pp-review-i");
        HashMap<String, String[]> sentiments = new HashMap<String, String[]>();

        for (Element review : reviews) {
            Elements star = review.select("span.g-rating-stars-i");
            Elements text = review.select("div.pp-review-text");
            Elements texts = text.select("div.pp-review-text-i");
            String stars = star.attr("content");
            ArrayList<String> value = getName();

            for (Element text1 : texts) {
                String text2 = text1.ownText();
                sentiments.put(value.get(counter), new String[]{text2, stars});
                counter++;
            }
        }
        for (String key : sentiments.keySet()) {
            for (String[] i : sentiments.values()) {
                    for (String j:i) {
                        System.out.println(key);
                        System.out.println(j);
                    }
            }
        }
        return sentiments;
    }

    public ArrayList getName() throws IOException {
        Document doc = Jsoup.connect(html).get();
        ArrayList<String> names = new ArrayList<String>();
        Elements tiles = doc.select("div.g-i-tile-i-title");
        for (Element tile : tiles) {
            Elements links = tile.select("a");
            names.add(links.text());
        }
        return names;
    }

}

class Folder {
    File folder = new File("C:/Users/Martoshka/Microwave/src/main/java/data");

    public Folder() throws IOException {
        this.folder = folder;
    }
    public void createFile() {
        boolean folderExists = folder.exists();
        if (!folderExists) {
            folderExists = folder.mkdirs();
        }
    }
}