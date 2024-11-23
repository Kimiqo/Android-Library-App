//package com.example.group1_projectwork;
//
//import android.content.Context;
//import android.util.JsonReader;
//import android.util.JsonWriter;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class StorageHelper {
//    private static final String FILE_NAME = "books.json";
//
//    public static void saveBooks(Context context, List<Book> books) {
//        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
//             JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"))) {
//            writer.beginArray();
//            for (Book book : books) {
//                writer.beginObject();
//                writer.name("title").value(book.getTitle());
//                writer.name("author").value(book.getAuthor());
//                writer.endObject();
//            }
//            writer.endArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static List<Book> loadBooks(Context context) {
//        List<Book> books = new ArrayList<>();
//        try (FileInputStream fis = context.openFileInput(FILE_NAME);
//             JsonReader reader = new JsonReader(new InputStreamReader(fis, "UTF-8"))) {
//            reader.beginArray();
//            while (reader.hasNext()) {
//                reader.beginObject();
//                String title = null, author = null, pdfUri = null;
//                while (reader.hasNext()) {
//                    String name = reader.nextName();
//                    if (name.equals("title")) {
//                        title = reader.nextString();
//                    } else if (name.equals("author")) {
//                        author = reader.nextString();
//                    } else if (name.equals("pdfUri")) {
//                        pdfUri = reader.nextString(); // Load the pdfUri
//                    } else {
//                        reader.skipValue();
//                    }
//                }
//                if (title != null && author != null && pdfUri != null) {
//                    books.add(new Book(title, author, pdfUri));
//                }
//                reader.endObject();
//            }
//            reader.endArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return books;
//    }
//
//}
