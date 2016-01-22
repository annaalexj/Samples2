package com.myapps.b.set.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.b.set.R;
import com.myapps.b.set.communication.HttpRequestConstants;
import com.myapps.b.set.model.Document;
import com.myapps.b.set.utils.Constants;

public class DocumentsAdapter extends BaseAdapter
{

	private Activity				activity;
	private ArrayList<Document>		lstDocument		= new ArrayList<Document>();
	private static LayoutInflater	inflater		= null;
	String							dashboardType	= "";

	public DocumentsAdapter(Activity argActivity, ArrayList<Document> argDocumentList, String argdashboardType)
	{
		dashboardType 			= argdashboardType;
		activity 				= argActivity;
		lstDocument 			= argDocumentList;
		inflater 				= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private class ViewHolder
	{
		TextView	txtFileName;
		TextView	txtDescription;
		ImageView	imgFileType;
		ImageView	imgThumbsUp;
	}

	public int getCount()
	{
		return lstDocument.size();
	}

	public Object getItem(int index)
	{
		return lstDocument.get(index);
	}

	public long getItemId(int arg0)
	{
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row 				= null;
		ViewHolder holder 		= null;
		Document doc 			= (Document) getItem(position);
		if (convertView == null)
		{
			holder 					= new ViewHolder();
			row 					= inflater.inflate(R.layout.document_list_cell, null);
			holder.txtFileName 		= (TextView) row.findViewById(R.id.txtFileName);
			holder.txtDescription 	= (TextView) row.findViewById(R.id.txtDescription);
			holder.imgFileType 		= (ImageView) row.findViewById(R.id.imgFileType);
			holder.imgThumbsUp 		= (ImageView) row.findViewById(R.id.imgThumbsUp);
			row.setTag(holder);
		}
		else
		{
			row 					= convertView;
			holder 					= (ViewHolder) convertView.getTag();
		}

		String docName 				= "";

		holder.imgThumbsUp.setVisibility(View.GONE);
		if (doc.getRecommendedDocStatus())
		{
			holder.imgThumbsUp.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.imgThumbsUp.setVisibility(View.GONE);
		}
		if (dashboardType.equalsIgnoreCase(HttpRequestConstants.ACTION_ALL_SET_RECOMMANDED))
		{
			docName 		= doc.gettingFileName(doc.getAssetname());
		}
		else if (dashboardType.equalsIgnoreCase(Constants.SEARCH_LABEL))
		{
			docName 		= doc.getLinkFilename();
		}
		else if (dashboardType.contains("category_"))
		{
			if (dashboardType.equalsIgnoreCase("category_Proposal Tracker"))
			{
				docName 	= doc.gettingFileName(doc.getLinkFilename());
			}
			else if (dashboardType.equalsIgnoreCase("category_AVM Links"))
			{
				docName 	= doc.gettingFileName(doc.getLinkFilename());
			}
			else
				docName 	= doc.getAssetname();
		}
		else
		{
			docName 		= doc.gettingFileName(doc.getLinkFilename());
		}

		if (docName == null || docName.equals(""))
		{
			docName 		= doc.gettingFileName(doc.getLinkFilename());
		}

		if (dashboardType.equalsIgnoreCase("zip"))
		{
			holder.txtDescription.setVisibility(View.GONE);
		}
		if (dashboardType.equalsIgnoreCase(HttpRequestConstants.ACTION_MOBILE_APP))
		{
			holder.imgThumbsUp.setVisibility(View.GONE);
		}
		if (docName != null && !docName.equals("") && docName.contains("_")) docName = docName.replace("_", " ");
		doc.setFileName(docName);

		holder.txtFileName.setText(docName);
		String description 				= doc.getDescription();
		if (description != null && !description.equals(""))
		{
			Spanned descriptionToHtml 	= Html.fromHtml(doc.getDescription());
			holder.txtDescription.setText(descriptionToHtml.toString());
		}
		String filetype 				= doc.getFiletype();
		if (filetype != null)
		{
			if (filetype.equalsIgnoreCase("PPTX") || filetype.equalsIgnoreCase("PPT") || filetype.equalsIgnoreCase("POT") || filetype.equalsIgnoreCase("POTX"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_ppt);
			}
			else if (filetype.equalsIgnoreCase("DOCX") || filetype.equalsIgnoreCase("DOC"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_word);
			}
			else if (filetype.equalsIgnoreCase("XLSX") || filetype.equalsIgnoreCase("XLS"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_exl);
			}
			else if (filetype.equalsIgnoreCase("PDF"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_pdf);
			}
			else if (filetype.equalsIgnoreCase("ZIP"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_zip);
			}
			else if (filetype.equalsIgnoreCase("MP4") || filetype.equalsIgnoreCase("3GP") || filetype.equalsIgnoreCase("AVI") || filetype.equalsIgnoreCase("WMV")
					|| filetype.equalsIgnoreCase("YOUTUBE"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_1_video);
			}
			else if (filetype.equalsIgnoreCase("JPG") || filetype.equalsIgnoreCase("JPEG") || filetype.equalsIgnoreCase("PNG") || filetype.equalsIgnoreCase("BMP")
					|| filetype.equalsIgnoreCase("GIF"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_1_image);
			}
			else if (filetype.equalsIgnoreCase("web"))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_1_html);
			}
			else if (filetype.equalsIgnoreCase("folder") || filetype.equalsIgnoreCase(""))
			{
				holder.imgFileType.setImageResource(R.drawable.icon_folder);
			}
			else if (filetype.equalsIgnoreCase(Constants.SEARCH_LABEL))
			{
				holder.imgFileType.setVisibility(View.GONE);
			}
			else
			{
				holder.imgFileType.setImageResource(R.drawable.icon_unknown);
			}
		}
		else
		{
			holder.imgFileType.setImageResource(R.drawable.icon_unknown);
		}
		return row;
	}

}
