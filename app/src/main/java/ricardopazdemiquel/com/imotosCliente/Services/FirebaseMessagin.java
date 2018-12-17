package ricardopazdemiquel.com.imotosCliente.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import ricardopazdemiquel.com.imotosCliente.PedirSieteMap;
import ricardopazdemiquel.com.imotos.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ricardopazdemiquel.com.imotosCliente.utiles.Contexto;

public class FirebaseMessagin extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()==0){
            return;
        }
        switch (remoteMessage.getData().get("evento")){
            case "confirmar_carrera":
                confirmar_carrera(remoteMessage);
                break;
            case "mensaje":
                mensaje(remoteMessage);
                break;
            case "conductor_cerca":
                try {
                    conductor_cerca(remoteMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "conductor_llego":
                conductor_llego(remoteMessage);
                break;
            case "Inicio_Carrera":
                Inicio_Carrera(remoteMessage);
                break;
            case "Finalizo_Carrera":
                Finalizo_Carrera(remoteMessage);
                break;
            case "Carrera_Cancelada":
                Cancelo_carrera(remoteMessage);
                break;
            case "confirmo_compra":
                confirmo_compra(remoteMessage);
                break;
            case "agrego_costo_extra":
                agrego_costo_extra(remoteMessage);
                break;
            case "elimino_costo_extra":
                elimino_costo_extra(remoteMessage);
                break;
            case "mensaje_recibido":
                mensaje_recibido(remoteMessage);
                break;
        }
        return;
    }



    private void Finalizo_Carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        notificationIntent.putExtra("carrera",remoteMessage.getData().get("json"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("Tu viaje ha finalizado.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.putExtra("carrera",remoteMessage.getData().get("json"));
        intent.setAction("Finalizo_Carrera");
        sendBroadcast(intent);
    }


    private void Inicio_Carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("Tu viaje está en curso." +
                        "Que tengas un buen viaje.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("Inicio_Carrera");
        sendBroadcast(intent);
    }

    private void conductor_llego(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("Tu iMoto ya llegó.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("conductor_llego");
        sendBroadcast(intent);
    }

    private void conductor_cerca(RemoteMessage remoteMessage) throws JSONException {
        String s  = remoteMessage.getData().get("json");
        Intent notificationIntent;
            JSONObject object = new JSONObject(s);
            int valor = (int) object.get("id_tipo");
            if(valor == 2){
                notificationIntent= new Intent(this, PedirSieteMap.class);
            }else{
                notificationIntent = new Intent(this, PedirSieteMap.class);
            }
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("Tu iMoto está cerca.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("conductor_cerca");
        sendBroadcast(intent);
    }

    private void confirmar_carrera(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        JSONObject json = null;
        try {
            json = new JSONObject(remoteMessage.getData().get("json"));
            intent.putExtra("json" , json.toString());

            JSONObject jsonUsuario = new JSONObject(remoteMessage.getData().get("jsonUsuario"));

            intent.putExtra("jsonUsuario" , jsonUsuario.toString());
            intent.setAction("confirmar_carrera");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void Cancelo_carrera(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("El conductor canceló el viaje. Disculpa las molestias.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("cancelo_carrera");
        sendBroadcast(intent);
    }

    private void confirmo_compra(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                .setContentTitle("iMoto")
                .setContentText("Ya compramos tu pedido.")
                .setSmallIcon(R.drawable.ic_icon_imoto)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
        Intent intent = new Intent();
        intent.putExtra("obj_carrera",remoteMessage.getData().get("json"));
        intent.setAction("Confirmo_compra");
        sendBroadcast(intent);
    }

    private void agrego_costo_extra(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        try {
            JSONObject obj = new JSONObject(remoteMessage.getData().get("json"));
            Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                    .setContentTitle("iMoto")
                    .setContentText("Se agregó \""+obj.getString("nombre")+"\" como costo extra, con un valor de Bs. "+obj.getString("costo")+".")
                    .setSmallIcon(R.drawable.ic_icon_imoto)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2,notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void elimino_costo_extra(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, PedirSieteMap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        try {
            JSONObject obj = new JSONObject(remoteMessage.getData().get("json"));
            Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                    .setContentTitle("iMoto")
                    .setContentText("Se eliminó \""+obj.getString("nombre")+"\" de tus costos extras.")
                    .setSmallIcon(R.drawable.ic_icon_imoto)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2,notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void mensaje(RemoteMessage remoteMessage){
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("Message");
        sendBroadcast(intent);
    }



    private void Carrera_terminada(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        JSONObject json = null;
        try {
            json = new JSONObject(remoteMessage.getData().get("json"));
            intent.putExtra("json" , json.toString());
            intent.setAction("confirmar_carrera");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void mensajeCT(RemoteMessage remoteMessage){
        Intent intent = new Intent();
        intent.putExtra("message",remoteMessage.getData().get("mensaje"));
        intent.setAction("Message");
        sendBroadcast(intent);
    }
    private void mensaje_recibido(RemoteMessage remoteMessage) {
        try {
            JSONObject obj = new JSONObject(remoteMessage.getData().get("json"));
            setMensaje(obj);
            Intent notificationIntent = new Intent(this, PedirSieteMap.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
            Notification notification= new NotificationCompat.Builder(this, Contexto.CHANNEL_ID)
                    .setContentTitle("iMoto: Nuevo mensaje.")
                    .setContentText(obj.getString("mensaje"))
                    .setSmallIcon(R.drawable.ic_icon_imoto)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(3,notification);
            Intent intent = new Intent();
            intent.putExtra("obj",obj.toString());
            intent.setAction("nuevo_mensaje");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setMensaje(JSONObject mensaje){
        JSONArray mensajes= getChat();
        if(mensajes==null){
            mensajes=new JSONArray();
        }
        mensajes.put(mensaje);
        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("chat_carrera", mensajes.toString());
        editor.commit();

    }
    public JSONArray getChat() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("chat_carrera", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONArray chat = new JSONArray(usr);
                return chat;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
