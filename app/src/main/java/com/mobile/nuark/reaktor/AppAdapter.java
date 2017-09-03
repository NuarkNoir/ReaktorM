package com.mobile.nuark.reaktor;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder>{
    private final Activity context;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ArrayList<MPost> imagesPostsList = new ArrayList<>();

    AppAdapter(Activity context, ArrayList<MPost> imagesPostsList) {
        this.context = context;
        this.imagesPostsList = imagesPostsList;
    }

    @Override
    public AppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imagepostitem, parent, false));
    }

    @Override
    public void onBindViewHolder(final AppAdapter.ViewHolder holder, final int position) {
        final int id = new Random(123124).nextInt();
        final String url = "http://dev.nuarknoir.h1n.ru/api/reactor/imgview.php?l=" + imagesPostsList.get(position).getImageUrl();
        imageView = holder.itemView.findViewById(R.id.image);
        progressBar = holder.itemView.findViewById(R.id.loadingProgress);
        TextView author = holder.itemView.findViewById(R.id.postauthor);
        author.setText(String.format("Автор поста - %s", imagesPostsList.get(position).getAuthor()));
        Ion.with(context).load(url).progressBar(progressBar).intoImageView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp[] = url.split("/");
                final String filename = Uri.decode(tmp[tmp.length-1]).replace("|", "").replace("\"", "").replace("/", "").replace(":", "").replace("?", "").replace("<", "").replace(">", "").replace("\\", "").replace("*", "");
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Скачать это изображение?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Reactor/";
                        if (!new File(path).exists())
                            if (!new File(path).mkdir())
                                Toast.makeText(context, "Error while creating directory for pictures!", Toast.LENGTH_SHORT).show();
                        final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context);
                        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        mNotificationBuilder.setContentTitle("Скачивание файла")
                                .setContentText("Название: " + filename).setSubText("Прогресс: ...")
                                .setSmallIcon(R.mipmap.ic_launcher).setColor(Color.YELLOW);
                        notificationManager.notify(id, mNotificationBuilder.build());
                        Ion.with(context).load(url)
                                .progress(new ProgressCallback() {
                                    @Override
                                    public void onProgress(long l, long l1) {
                                        mNotificationBuilder.setSubText("Прогресс: " + l + " из " + l1 + " байтов.");
                                        notificationManager.notify(id, mNotificationBuilder.build());
                                    }
                                })
                                .write(new File(path + filename))
                                .setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File file) {
                                        if (e != null) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            mNotificationBuilder.setContentText(e.getMessage()).setSubText("Ошибка!").setColor(Color.RED);
                                            notificationManager.notify(id, mNotificationBuilder.build());
                                        }
                                        else {
                                            mNotificationBuilder.setContentText("Файл скачан!").setSubText("Успех!").setColor(Color.GREEN);
                                            notificationManager.notify(id, mNotificationBuilder.build());
                                        }
                                    }
                                });
                    }
                }).setNeutralButton("Перезагрузить изображение", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Started reloading image...\n" + url, Toast.LENGTH_SHORT).show();
                        progressBar.setProgress(0);
                        Ion.with(context).load(url).progressBar(progressBar).intoImageView(imageView);
                    }
                }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesPostsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
