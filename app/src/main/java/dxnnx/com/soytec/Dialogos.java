package dxnnx.com.soytec;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by DXNNX on 9/7/17.
 */

public class Dialogos {
    public static void Alerta(String Titulo, String Mensaje, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        try {
            alertDialog.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
