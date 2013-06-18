package com.cover.ecardroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Editor extends Activity implements OnClickListener,
		OnTouchListener {
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private Uri mImageCaptureUri;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	private final int listCard_ID = 1;
	private final int menuQuote_ID = 2;
	private IconContextMenu ic = null;

	private TextView quote;
	private ImageView imgHolder;
	private ImageView imgFrame;

	private ImageButton bPhoto;
	private ImageButton bCard;
	private ImageButton bText;
	private ImageButton bReset;
	private ImageButton bShare;

	int[] imgArr = null;
	int imgC1[] = { R.drawable.c1_0, R.drawable.c1_1, R.drawable.c1_2,
			R.drawable.c1_3, R.drawable.c1_4 };
	int imgC2[] = { R.drawable.c2_0, R.drawable.c2_1, R.drawable.c2_2,
			R.drawable.c2_3, R.drawable.c2_4, R.drawable.c2_5 };
	int imgC3[] = { R.drawable.c3_00, R.drawable.c3_01, R.drawable.c3_02,
			R.drawable.c3_03, R.drawable.c3_04, R.drawable.c3_05,
			R.drawable.c3_06, R.drawable.c3_07, R.drawable.c3_08,
			R.drawable.c3_09, R.drawable.c3_10 };
	int imgC4[] = { R.drawable.c4_0, R.drawable.c4_1, R.drawable.c4_2 };
	int imgC5[] = { R.drawable.c5_0, R.drawable.c5_1, R.drawable.c5_2,
			R.drawable.c5_3, R.drawable.c5_4, R.drawable.c5_5, R.drawable.c5_6 };
	int imgC6[] = { R.drawable.c6_0, R.drawable.c6_1, R.drawable.c6_2,
			R.drawable.c6_3, R.drawable.c6_4, R.drawable.c6_5, R.drawable.c6_6,
			R.drawable.c6_7, R.drawable.c6_8, R.drawable.c6_9 };
	int imgC7[] = { R.drawable.c7_0, R.drawable.c7_1, R.drawable.c7_2 };
	RefreshHandler refreshHandler = new RefreshHandler();
	int i = 0;

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (imgArr.length == 0) {
				imgArr = imgC1;
			}
			Editor.this.updateUI(imgArr);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public void updateUI(int[] arr) {
		refreshHandler.sleep(500);
		if (i == arr.length) {
			i = 0;
		} else {
			try {
				imgFrame.setImageResource(arr[i]);
				i++;
			} catch (Exception e) {
				i = 0;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.editor);
		Bundle ex = getIntent().getExtras();
		String cardId = ex.getString("typeCard");

		quote = (TextView) findViewById(R.id.quote);
		imgHolder = (ImageView) findViewById(R.id.imgHold);
		imgFrame = (ImageView) findViewById(R.id.imgFrame);
		bPhoto = (ImageButton) findViewById(R.id.TbPhoto);
		bCard = (ImageButton) findViewById(R.id.TbCard);
		bText = (ImageButton) findViewById(R.id.TbText);
		bReset = (ImageButton) findViewById(R.id.TbReset);
		bShare = (ImageButton) findViewById(R.id.TbShare);

		bPhoto.setOnClickListener(this);
		bCard.setOnClickListener(this);
		bText.setOnClickListener(this);
		bReset.setOnClickListener(this);
		bShare.setOnClickListener(this);

		quote.setShadowLayer(2, 0, 0, Color.BLACK);

		imgHolder.setOnTouchListener(this);
		//quote.setOnTouchListener(this);
		if (cardId.equals("Birthday")) {
			quote.setText("Happy Birthday to You, Have a Blessed Day...");
			imgArr = imgC5;
		} else if (cardId.equals("Graduation")) {
			quote.setText("Loads of Wishes... You Have the Courage to Pursue Them");
			imgArr = imgC3;
		} else if (cardId.equals("Anniversary")) {
			quote.setText("Happy Anniversary... Hoping The Love, Bringing Joy");
			imgArr = imgC4;
		} else if (cardId.equals("Congratulation")) {
			quote.setText("Congratulation... You Have landed your Dream");
			imgArr = imgC2;
		} else if (cardId.equals("Sympathy")) {
			quote.setText("Felt Condolances and Fondest Remembrances");
			imgArr = imgC1;
		} else {
			quote.setText("Your Quote Message Here...");
			imgArr = imgC6;
		}
		updateUI(imgArr);
	}

	public void DialogPicture() {
		final String[] items = new String[] { "From Camera", "From SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file = new File(Environment
							.getExternalStorageDirectory(), "tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg");
					mImageCaptureUri = Uri.fromFile(file);

					try {
						intent.putExtra(
								android.provider.MediaStore.EXTRA_OUTPUT,
								mImageCaptureUri);
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (Exception e) {
						e.printStackTrace();
					}

					dialog.cancel();
				} else {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == listCard_ID) {
			return ic.createMenu("Choose Card");
		} else if (id == menuQuote_ID) {
			return ic.createMenu("Quote Editor");
		}
		return super.onCreateDialog(id);
	}

	public void DialogCard() {
		ic = new IconContextMenu(this, listCard_ID);
		Resources res = getResources();
		ic.addItem(res, "Leaf Glitter", R.drawable.c1, 1);
		ic.addItem(res, "RedTro", R.drawable.c2, 2);
		ic.addItem(res, "Minimalist White", R.drawable.c3, 3);
		ic.addItem(res, "Rose Glitter", R.drawable.c4, 4);
		ic.addItem(res, "Techno Lights", R.drawable.c5, 5);
		ic.addItem(res, "Bubble", R.drawable.c6, 6);
		ic.addItem(res, "Stars", R.drawable.c7, 7);
		ic.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				switch (menuId) {
				case 1:
					imgArr = imgC1;
					break;
				case 2:
					imgArr = imgC2;
					break;
				case 3:
					imgArr = imgC3;
					break;
				case 4:
					imgArr = imgC4;
					break;
				case 5:
					imgArr = imgC5;
					break;
				case 6:
					imgArr = imgC6;
					break;
				case 7:
					imgArr = imgC7;
					break;
				}
			}
		});
		showDialog(listCard_ID);
	}

	public void DialogFont() {
		ic = new IconContextMenu(this, menuQuote_ID);
		Resources res = getResources();
		ic.addItem(res, "Quote", R.drawable.quote_text, 1);
		ic.addItem(res, "Size", R.drawable.quote_size, 2);
		ic.addItem(res, "Typeface", R.drawable.quote_typeface, 3);
		ic.addItem(res, "Color", R.drawable.quote_color, 4);
		ic.addItem(res, "Shadow Color", R.drawable.quote_shadow, 5);
		ic.addItem(res, "Align", R.drawable.quote_align, 6);
		ic.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				switch (menuId) {
				case 1:
					changeQuote();
					break;
				case 2:
					changeQuoteSize();
					break;
				case 3:
					changeTypeface();
					break;
				case 4:
					changeQuoteColor();
					break;
				case 5:
					changeShadowColor();
					break;
				case 6:
					changeQuoteAlign();
					break;
				}
			}
		});
		showDialog(menuQuote_ID);
	}

	public void changeQuote() {
		final AlertDialog.Builder aQuote = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		aQuote.setTitle("Quote Edit");
		aQuote.setMessage("Insert Quote Here!");
		aQuote.setView(input);
		input.setText(quote.getText());
		aQuote.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				quote.setText(value);
			}
		});
		aQuote.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		aQuote.show();
	}

	public void changeQuoteSize() {
		final AlertDialog.Builder aSize = new AlertDialog.Builder(this);
		final Spinner iSize = new Spinner(this);
		aSize.setTitle("Font Size");
		aSize.setView(iSize);
		Float fSize[] = { 18.0f, 20.0f, 22.0f, 24.0f, 26.0f, 30.0f, 36.0f };
		ArrayAdapter<Float> adapterFont = new ArrayAdapter<Float>(this,
				android.R.layout.simple_spinner_item, fSize);
		iSize.setAdapter(adapterFont);
		iSize.setSelection(adapterFont.getPosition(quote.getTextSize()));
		aSize.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				float rSize = (Float) iSize.getSelectedItem();
				quote.setTextSize(rSize);
			}
		});
		aSize.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		aSize.show();
	}

	public void changeTypeface() {
		final String[] listFonts = { "Bodoni", "Comic", "Lucia BT",
				"Monotype Corsiva", "Pristina" };

		AlertDialog.Builder aFace = new AlertDialog.Builder(this);
		aFace.setTitle("Pick a Typeface");
		aFace.setItems(listFonts, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				Typeface t = Typeface.createFromAsset(getAssets(), "fonts/"
						+ listFonts[item] + ".ttf");
				quote.setTypeface(t);
			}
		});
		aFace.show();
	}

	public void changeQuoteColor() {
		final String[] listColor = { "White", "Black", "Red", "Green", "Blue",
				"Gray" };
		AlertDialog.Builder aColor = new AlertDialog.Builder(this);
		aColor.setTitle("Pick a Color");
		aColor.setItems(listColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					quote.setTextColor(Color.WHITE);
					break;
				case 1:
					quote.setTextColor(Color.BLACK);
					break;
				case 2:
					quote.setTextColor(Color.RED);
					break;
				case 3:
					quote.setTextColor(Color.GREEN);
					break;
				case 4:
					quote.setTextColor(Color.BLUE);
					break;
				case 5:
					quote.setTextColor(Color.GRAY);
					break;
				}
			}
		});
		aColor.show();
	}

	public void changeShadowColor() {
		final String[] listColor = { "Black", "White", "Magenta",
				"Light Yellow", "Gray", "Transparent" };
		AlertDialog.Builder sColor = new AlertDialog.Builder(this);
		sColor.setTitle("Pick a Color");
		sColor.setItems(listColor, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					quote.setShadowLayer(2, 0, 0, Color.BLACK);
					break;
				case 1:
					quote.setShadowLayer(2, 0, 0, Color.WHITE);
					break;
				case 2:
					quote.setShadowLayer(2, 0, 0, Color.MAGENTA);
					break;
				case 3:
					quote.setShadowLayer(2, 0, 0, Color.rgb(255, 224, 139));

					break;
				case 4:
					quote.setShadowLayer(2, 0, 0, Color.GRAY);
					break;
				case 5:
					quote.setShadowLayer(1, 0, 0, Color.TRANSPARENT);
					break;
				}
			}
		});
		sColor.show();
	}

	public void changeQuoteAlign() {
		final String[] listAlignment = { "Center", "Left", "Right", "Justify" };

		AlertDialog.Builder aAlign = new AlertDialog.Builder(this);
		aAlign.setTitle("Pick a Alignment");
		aAlign.setItems(listAlignment, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					quote.setGravity(Gravity.CENTER);
					break;
				case 1:
					quote.setGravity(Gravity.LEFT);
					break;
				case 2:
					quote.setGravity(Gravity.RIGHT);
					break;
				case 3:
					quote.setGravity(Gravity.FILL_HORIZONTAL);
					break;
				}
			}
		});
		aAlign.show();
	}

	public void getCard(String fileName) {
		Bitmap bitmap;
		View editorView = findViewById(R.id.card);
		editorView.setDrawingCacheEnabled(true);
		bitmap = editorView.getDrawingCache();
		OutputStream fout = null;

		File imageFile = new File(Environment.getExternalStorageDirectory()
				.toString() + "/", fileName + ".jpg");
		try {
			fout = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
			fout.flush();
			fout.close();
			if (fileName != "temp") {
				Toast.makeText(this, "Save to " + imageFile.getAbsolutePath(),
						Toast.LENGTH_SHORT).show();
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void ShareVia() {
		final String[] items = new String[] { "Share", "Save to SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Share & Save");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					getCard("temp");
					Intent sharingIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					sharingIntent.setType("image/jpeg");
					sharingIntent.putExtra(Intent.EXTRA_STREAM,
							Uri.parse("file:///sdcard/temp.jpg"));
					startActivity(Intent.createChooser(sharingIntent,
							"Share via"));
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"dd-MM-yyyy_HH:mm:ss");
					String title = sdf.format(new Date());
					getCard("eCard"+title);
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		Bitmap bitmap = null;

		String path = "";

		if (requestCode == PICK_FROM_FILE) {
			mImageCaptureUri = data.getData();
			path = getRealPathFromURI(mImageCaptureUri); // from Gallery

			if (path == null)
				path = mImageCaptureUri.getPath(); // from File Manager

			if (path != null)
				bitmap = BitmapFactory.decodeFile(path);
		} else {
			path = mImageCaptureUri.getPath();
			bitmap = BitmapFactory.decodeFile(path);
		}
		imgHolder.setImageBitmap(bitmap);
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		if (cursor == null) {
			return null;
		}
		int column_index = cursor
				.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	public void onClick(View v) {
		if (v == bPhoto) {
			DialogPicture();
		} else if (v == bCard) {
			DialogCard();
		} else if (v == bText) {
			DialogFont();
		} else if (v == bReset) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Reset Editor?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									quote.setText("Type Your Text Here...");
									imgHolder
											.setImageResource(R.drawable.baseholder);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

		} else if (v == bShare) {
			ShareVia();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		//ImageView view2 = (ImageView) v;
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), R.id.quote);
		//view2.setImageBitmap(bm);
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		//view2.setImageMatrix(matrix);
		return true;
	}

	// Determine the space between the first two fingers
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// Calculate the mid point of the first two fingers
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
