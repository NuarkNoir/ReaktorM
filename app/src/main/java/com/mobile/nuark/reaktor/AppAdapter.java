package com.mobile.nuark.reaktor;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder>{
    private final Activity context;
    private ImageView imageView;
    private ArrayList<MPost> imagesPostsList = new ArrayList<>();

    public AppAdapter(Activity context, ArrayList<MPost> imagesPostsList) {
        this.context = context;
        this.imagesPostsList = imagesPostsList;
    }

    @Override
    public AppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagepostitem, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AppAdapter.ViewHolder holder, final int position) {
        imageView = (ImageView) holder.itemView.findViewById(R.id.image);
        TextView author = (TextView) holder.itemView.findViewById(R.id.postauthor);
        author.setText("Автор поста - " + imagesPostsList.get(position).getAuthor());
        Ion.with(context).load("http://dev.nuarknoir.h1n.ru/api/reactor/imgview.php?l=" + imagesPostsList.get(position).getImageUrl()).progressBar((ProgressBar) holder.itemView.findViewById(R.id.loadingProgress)).intoImageView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = "http://dev.nuarknoir.h1n.ru/api/reactor/imgview.php?l=" + imagesPostsList.get(position).getImageUrl();
                String tmp[] = url.split("/");
                final String filename = Uri.decode(tmp[tmp.length-1]).replace("|", "").replace("\"", "").replace("/", "").replace(":", "").replace("?", "").replace("<", "").replace(">", "").replace("\\", "").replace("*", "");
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Скачать это изображение?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(context, "Запрашиваем доступ к записи на карту\n" +
                                    "Пожалуйста, для корректной работы приложения, предоставьте разрешение", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(
                                    context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    1
                            );
                        }
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        new File(path + "/Pictures/Reactor").mkdir();
                        path = path + "/Pictures/Reactor/";
                        final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context);
                        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        mNotificationBuilder.setContentTitle("Скачивание файла");
                        mNotificationBuilder.setContentText("Название: " + filename);
                        mNotificationBuilder.setSubText("Прогресс: ...");
                        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher).setColor(Color.YELLOW);
                        notificationManager.notify(1337, mNotificationBuilder.build());
                        Ion.with(context).load(url)
                                .progress(new ProgressCallback() {
                                    @Override
                                    public void onProgress(long l, long l1) {
                                        mNotificationBuilder.setSubText("Прогресс: " + l + " из " + l1 + " байтов.");
                                        notificationManager.notify(1337, mNotificationBuilder.build());
                                        System.out.println(l + "   " + l1);
                                    }
                                })
                                .write(new File(path + filename))
                                .setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File file) {
                                        if (e != null) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            mNotificationBuilder.setContentText(e.getMessage());
                                            mNotificationBuilder.setSubText("Ошибка!").setColor(Color.RED);
                                            notificationManager.notify(1337, mNotificationBuilder.build());
                                        }
                                        else {
                                            mNotificationBuilder.setContentText("Файл скачан!");
                                            mNotificationBuilder.setSubText("Успех!").setColor(Color.GREEN);
                                            notificationManager.notify(1337, mNotificationBuilder.build());
                                        }
                                    }
                                });
                    }
                }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesPostsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
