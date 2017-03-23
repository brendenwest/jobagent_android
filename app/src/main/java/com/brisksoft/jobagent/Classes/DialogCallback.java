package com.brisksoft.jobagent.Classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class DialogCallback implements DialogInterface.OnClickListener, Runnable
{
	
  public DialogCallback(Context c)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(c);
    builder.setMessage(com.brisksoft.jobagent.R.string.confirm_delete)
      .setPositiveButton("Yes", this)
      .setNegativeButton("No", this)
      .setCancelable(true);
    builder.create().show();
  }

  public void onClick(DialogInterface dialog, int which)
  {
    dialog.dismiss();
    if (which == DialogInterface.BUTTON_POSITIVE) run();
  }
  

}