package com.example.ratio.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.example.ratio.Dialogs.BaseDialog;
import com.example.ratio.Dialogs.BasicDialog;
import com.example.ratio.Entities.ProjectSubcategoryEntity;
import com.example.ratio.Entities.ProjectsEntity;
import com.example.ratio.Entities.ServicesEntity;
import com.example.ratio.Enums.CloudClassNames;
import com.example.ratio.Entities.ProjectTypeEntity;
import com.example.ratio.Enums.PROJECT;
import com.example.ratio.Enums.PROJECT_TYPE;
import com.example.ratio.Enums.PROJECT_TYPE_SUBCATEGORY;
import com.example.ratio.Enums.SERVICES;
import com.example.ratio.R;
import com.example.ratio.Dialogs.CheckBoxDialog;
import com.example.ratio.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddNew extends Fragment {

    private static final String TAG = "FragmentAddNew";
    @BindView(R.id.addnew_field_projectname) EditText addnew_field_projectname;
    @BindView(R.id.addnew_field_projectcode) EditText addnew_field_projectcode;
    @BindView(R.id.addnew_field_projectowner) EditText addnew_field_projectowner;
    @BindView(R.id.addnew_spinner_typeofproject) MaterialSpinner addnew_spinner_typeofproject;
    @BindView(R.id.addnew_spinner_subcategory) MaterialSpinner addnew_spinner_subcategory;
    @BindView(R.id.addnew_spinner_services) MaterialSpinner addnew_spinner_services;
    @BindView(R.id.addnew_button_create) Button addnew_button_create;
    @BindView(R.id.addnew_field_specificservice) EditText addnew_field_specificservice;
    @BindView(R.id.addnew_field_specifictype) EditText addnew_field_specifictype;
    @BindView(R.id.addnew_field_specificsubcategory) EditText addnew_field_specificsubcategory;
    @BindView(R.id.addnew_imageview_thumbnail) ImageView addnew_imageview_thumbnail;
    @BindView(R.id.addnew_checkbox_active) CheckBox addnew_checkbox_active;
    @BindView(R.id.addnew_checkbox_archived) CheckBox addnew_checkbox_archived;
    @BindView(R.id.addnew_checkbox_proposal) CheckBox addnew_checkbox_proposal;
    ProjectTypeEntity OTHERSCHOICE_TYPESOFPROJECT = new ProjectTypeEntity(null, "Others", true);
    ProjectSubcategoryEntity OTHERSCHOICE_SUBCATEGORY = new ProjectSubcategoryEntity(null, "Others", true, null);
    ServicesEntity OTHERSCHOICE_SERVICES = new ServicesEntity(null, "Others", true);
    String selectedTypeOfProject = null;
    String selectedSubcategory = null;
    String selectedServices = null;
    BaseDialog dialog = null;
    BaseDialog checkBoxDialog = null;
    public FragmentAddNew() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new, container, false);
        ButterKnife.bind(this, view);
        List<ProjectTypeEntity> projectTypes = ProjectTypeEntity.listAll(ProjectTypeEntity.class); // retrieve locally persisted ProjectTypeEntities
        List<ProjectSubcategoryEntity> projectSubcategoryEntities = ProjectSubcategoryEntity.listAll(ProjectSubcategoryEntity.class); // retrieve locally persisted ProjectSubcategoryEntity
        List<ServicesEntity> servicesEntities = ServicesEntity.listAll(ServicesEntity.class);// retrieve locally persisted ServicesEntity
        Log.d(TAG, "onCreateView: ProjectTypeEntity size: " + String.valueOf(projectTypes.size()));
        Log.d(TAG, "onCreateView: ProjectSubcategoryEntity size: " + String.valueOf(projectSubcategoryEntities.size()));
        Log.d(TAG, "onCreateView: ServicesEntity size: " + String.valueOf(servicesEntities.size()));
        addnew_checkbox_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    addnew_checkbox_active.setError(null);
                    addnew_checkbox_archived.setError(null);
                    addnew_checkbox_proposal.setError(null);
                }
            }
        });
        addnew_checkbox_archived.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    addnew_checkbox_active.setError(null);
                    addnew_checkbox_archived.setError(null);
                    addnew_checkbox_proposal.setError(null);
                }
            }
        });
        addnew_checkbox_proposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    addnew_checkbox_active.setError(null);
                    addnew_checkbox_archived.setError(null);
                    addnew_checkbox_proposal.setError(null);
                }
            }
        });
        if(projectTypes.size() <= 0) {
            new RetrieveProjectTypesTask().execute((Void)null);
        }else{
            Collections.sort(projectTypes);
            projectTypes.add(projectTypes.size(), OTHERSCHOICE_TYPESOFPROJECT);
            addnew_spinner_typeofproject.setItems(projectTypes);
        }
        if(projectSubcategoryEntities.size() <= 0) {
            new RetrieveSubCategoryTask().execute((Void) null);
        } else {
            Collections.sort(projectSubcategoryEntities);
            projectSubcategoryEntities.add(projectSubcategoryEntities.size(), OTHERSCHOICE_SUBCATEGORY);
            addnew_spinner_subcategory.setItems(projectSubcategoryEntities);
        }
        if(servicesEntities.size() <= 0) {
            new RetrieveServicesTask().execute((Void)null);
        } else {
            Collections.sort(servicesEntities);
            servicesEntities.add(servicesEntities.size(), OTHERSCHOICE_SERVICES);
            addnew_spinner_services.setItems(servicesEntities);
        }
        addnew_spinner_services.setOnItemSelectedListener(servicesListener());
        addnew_spinner_typeofproject.setOnItemSelectedListener(typeOfProjectListener());
        addnew_spinner_subcategory.setOnItemSelectedListener(subcategoryListener());
        return view;
    }
    @OnClick(R.id.addnew_imageview_thumbnail)
    public void imageChoose(View view) {
        String IS_NEED_CAMERA = new String();
        Intent intent1 = new Intent(getContext(), ImagePickActivity.class);
        intent1.putExtra(IS_NEED_CAMERA, true);
        intent1.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    addnew_imageview_thumbnail.setBackground(null);
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    for (ImageFile file : list) {
                        Picasso.get().load(new File(file.getPath())).into(addnew_imageview_thumbnail);
                    }
                } else {
                    Snackbar.make(getView(), "Error", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.addnew_button_create)
    public void createBtnClicked( View view ) {
        ProjectsEntity projectsEntity = new ProjectsEntity();
        if( !validateField(addnew_field_projectname) 
                | !validateField(addnew_field_projectcode) 
                | !validateField(addnew_field_projectowner) | !validateThumbnail(addnew_imageview_thumbnail)
            | !validateCheckbox() | !validateServices(selectedServices) | !validateTypeOfProject(selectedTypeOfProject)
            | !validateSubcategory(selectedSubcategory)) {
            return;
        }

        if(selectedServices.equalsIgnoreCase("Others")) {
            if(!validateField(addnew_field_specificservice)) {
                return;
            }
        }
        if(selectedTypeOfProject.equalsIgnoreCase("Others")) {
            if(!validateField(addnew_field_specifictype) | !validateField(addnew_field_specificsubcategory)) {
                return;
            }
        }
        if(selectedSubcategory.equalsIgnoreCase("Others")) {
            if(!validateField(addnew_field_specificsubcategory)) {
                return;
            }
        }
        projectsEntity.setProjectCode(addnew_field_projectcode.getText().toString());
        projectsEntity.setProjectName(addnew_field_projectname.getText().toString().toUpperCase());
        projectsEntity.setProjectOwner(addnew_field_projectowner.getText().toString().toUpperCase());
        Snackbar.make(getView(), "OK", Snackbar.LENGTH_SHORT).show();
        //new CreateProjectTask(projectsEntity).execute((Void)null);
    }

    private MaterialSpinner.OnItemSelectedListener servicesListener() {
        MaterialSpinner.OnItemSelectedListener listener = new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                addnew_spinner_services.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                selectedServices = item.toString();
                if(item instanceof ServicesEntity) {

                    addnew_field_specificservice.setVisibility(View.GONE);
                    if (((ServicesEntity) item).isOthers()) {  // if the chosen category is 'Others' then show hidden field
                        addnew_field_specificservice.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        return listener;
    }
    private MaterialSpinner.OnItemSelectedListener typeOfProjectListener() {
        MaterialSpinner.OnItemSelectedListener listener = new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                addnew_spinner_typeofproject.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                selectedTypeOfProject = item.toString();
                if(item instanceof ProjectTypeEntity) {
                    if(((ProjectTypeEntity) item).isOthers() == false){ // if the chosen category is NOT 'Others' then dont't show hidden field
                        addnew_field_specificsubcategory.setVisibility(View.GONE);
                        addnew_field_specifictype.setVisibility(View.GONE);
                        List<ProjectSubcategoryEntity> subs = ProjectSubcategoryEntity
                                .findWithQuery(ProjectSubcategoryEntity.class,
                                        "Select * from PROJECT_SUBCATEGORY_ENTITY where parent = ? Order by name ASC",
                                        ((ProjectTypeEntity) item).getObjectId());
                        Log.d(TAG, "onItemSelected: subs size: " + String.valueOf(subs.size()));
                        subs.add(subs.size(),
                                new ProjectSubcategoryEntity(null, "Others", true, null)); // Add 'Others' at the last index of array'
                        addnew_spinner_subcategory.setItems(subs);
                        addnew_spinner_subcategory.setVisibility(View.VISIBLE);
                    } else {
                        addnew_spinner_subcategory.setVisibility(View.GONE);
                        addnew_field_specifictype.setVisibility(View.VISIBLE);
                        addnew_field_specificsubcategory.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        return listener;
    }
    private MaterialSpinner.OnItemSelectedListener subcategoryListener() {
        MaterialSpinner.OnItemSelectedListener listener = new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                addnew_spinner_subcategory.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                selectedSubcategory = item.toString();
                if(item instanceof ProjectSubcategoryEntity){
                    if(((ProjectSubcategoryEntity) item).isOthers() == false) {// if the chosen category is NOT 'Others' then dont't show hidden field
                        addnew_field_specificsubcategory.setVisibility(View.GONE);
                    } else {
                        addnew_field_specificsubcategory.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        return listener;
    }
    private boolean validateField(EditText input) {
        String inputString = input.getText().toString().trim();

        if (inputString.isEmpty()) {
            input.setError("Field can't be empty");
            return false;
        } else {
            input.setError(null);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean validateThumbnail(ImageView imageView) {
        if(imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_person).getConstantState())) {
            imageView.setBackground(getResources().getDrawable(R.drawable.bg_error));
            return false;
        } else {
            imageView.setBackground(null);
            return true;
        }
    }
    private boolean validateCheckbox() {
        if(!addnew_checkbox_active.isChecked() && !addnew_checkbox_archived.isChecked() && !addnew_checkbox_proposal.isChecked()) {
            addnew_checkbox_active.setError("Choose at least 1");
            addnew_checkbox_archived.setError("Choose at least 1");
            addnew_checkbox_proposal.setError("Choose at least 1");
            return false;
        } else {
            addnew_checkbox_active.setError(null);
            addnew_checkbox_archived.setError(null);
            addnew_checkbox_proposal.setError(null);
            return true;
        }
    }

    private boolean validateServices(String selected) {
        if(selected == null) {
            addnew_spinner_services.setBackgroundColor(Color.RED);
            return false;
        } else {
            addnew_spinner_services.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            return true;
        }
    }
    private boolean validateTypeOfProject(String selected) {
        if(selected == null) {
            addnew_spinner_typeofproject.setBackgroundColor(Color.RED);
            return false;
        } else {
            addnew_spinner_typeofproject.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            return true;
        }
    }

    private boolean validateSubcategory(String selected) {
        if(selected == null) {
            addnew_spinner_subcategory.setBackgroundColor(Color.RED);
            return false;
        } else {
            addnew_spinner_subcategory.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            return true;
        }
    }




    private class RetrieveServicesTask extends AsyncTask<Void, Void, ArrayList<ServicesEntity>> {
        AlertDialog dialog;
        ArrayList<ServicesEntity> servicesEntities = new ArrayList<>();
        public RetrieveServicesTask() {
            dialog = Utility.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected ArrayList<ServicesEntity> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Operation Started");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(CloudClassNames.SERVICES.toString());
            try {
                Log.d(TAG, "doInBackground: Retrieving Services");
                List<ParseObject> result = query.find();
                Log.d(TAG, "doInBackground: result size: " + String.valueOf(result.size()));
                for(ParseObject parseObject : result) {
                    ServicesEntity servicesEntity = new ServicesEntity();
                    servicesEntity.setObjectId(parseObject.getObjectId());
                    servicesEntity.setName(parseObject.getString(SERVICES.NAME.toString()));
                    servicesEntity.setOthers(false);
                    servicesEntity.save();
                    servicesEntities.add(servicesEntity);
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Exception thrown: " + e.getMessage());
            }

            return servicesEntities;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ServicesEntity> servicesEntities) {
            Log.d(TAG, "onPostExecute: Operation done");
            Log.d(TAG, "onPostExecute: result size: " + String.valueOf(servicesEntities.size()));
            dialog.dismiss();
            if(servicesEntities.size() > 0) {
                servicesEntities.add(servicesEntities.size(), OTHERSCHOICE_SERVICES);
                addnew_spinner_services.setItems(servicesEntities);
            } else {
                Log.d(TAG, "onPostExecute: result empty");
            }
        }
    }
    private class RetrieveProjectTypesTask extends AsyncTask<Void, Void, ArrayList<ProjectTypeEntity>> {

        AlertDialog dialog;
        ArrayList<ProjectTypeEntity> projectTypeEntities = new ArrayList<>();

        public RetrieveProjectTypesTask() {
            Log.d(TAG, "RetrieveProjectTypesTask: Started");
            this.dialog = Utility.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected ArrayList<ProjectTypeEntity> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Retrieving types");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(CloudClassNames.PROJECT_TYPE.toString());
            try {
                Log.d(TAG, "doInBackground: Retrieving ProjectTypeEntity...");
                List<ParseObject> result = query.addAscendingOrder(PROJECT_TYPE.NAME.toString()).find();
                Log.d(TAG, "doInBackground: result size: " + String.valueOf(result.size()));
                for(ParseObject parseObject : result) {
                    ProjectTypeEntity projectTypeEntity = new ProjectTypeEntity();
                    projectTypeEntity.setObjectId(parseObject.getObjectId());
                    projectTypeEntity.setName(parseObject.getString(PROJECT_TYPE.NAME.toString()));
                    projectTypeEntity.setOthers(false);
                    projectTypeEntity.save();
                    projectTypeEntities.add(projectTypeEntity);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Exception thrown: " + e.getMessage());
            }
            return projectTypeEntities;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: load");
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ProjectTypeEntity> projectTypeEntities) {
            Log.d(TAG, "onPostExecute: Operation done");
            Log.d(TAG, "onPostExecute: result size: " + String.valueOf(projectTypeEntities.size()));
            dialog.dismiss();
            if(projectTypeEntities.size() > 0) {
                projectTypeEntities.add(projectTypeEntities.size(), new ProjectTypeEntity(null, "Others", true)); // Add 'Others' on the last position in array
                addnew_spinner_typeofproject.setItems(projectTypeEntities);
            }else {
                Log.d(TAG, "onPostExecute: Result is empty");
                Snackbar.make(getView(), "Result is empty", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class RetrieveSubCategoryTask extends AsyncTask<Void, Void, ArrayList<ProjectSubcategoryEntity>>{

        AlertDialog dialog;
        ArrayList<ProjectSubcategoryEntity> projectSubcategoryEntities = new ArrayList<>();
        public RetrieveSubCategoryTask() {
            dialog = Utility.getInstance().showLoading(getContext(), "Please wait", false);
            Log.d(TAG, "RetrieveSubCategoryTask: Started");
        }

        @Override
        protected ArrayList<ProjectSubcategoryEntity> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Opertion started");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(CloudClassNames.PROJECT_TYPE_SUBCATEGORY.toString());
            try {
                Log.d(TAG, "doInBackground: Retrieving ProjectSubcategoryEntity...");
                List<ParseObject> result = query.addAscendingOrder(PROJECT_TYPE_SUBCATEGORY.NAME.toString()).find();
                Log.d(TAG, "doInBackground: result size: " + String.valueOf(result.size()));
                for(ParseObject parseObject : result) {
                    ProjectSubcategoryEntity projectSubcategoryEntity = new ProjectSubcategoryEntity();
                    projectSubcategoryEntity.setObjectId(parseObject.getObjectId());
                    projectSubcategoryEntity.setName(parseObject.getString(PROJECT_TYPE_SUBCATEGORY.NAME.toString()));
                    projectSubcategoryEntity.setOthers(false);
                    projectSubcategoryEntity.setParent(parseObject.getString(PROJECT_TYPE_SUBCATEGORY.PARENT.toString()));
                    projectSubcategoryEntity.save();
                    projectSubcategoryEntities.add(projectSubcategoryEntity);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Exception throw " + e.getMessage());
            }
            return projectSubcategoryEntities;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ProjectSubcategoryEntity> projectSubcategoryEntities) {
            dialog.dismiss();
            Log.d(TAG, "onPostExecute: Operation done");
            Log.d(TAG, "onPostExecute: Result size: " + String.valueOf(projectSubcategoryEntities.size()));
            if(projectSubcategoryEntities.size() > 0) {
                this.projectSubcategoryEntities = projectSubcategoryEntities;
                //addnew_spinner_subcategory.setItems(projectSubcategoryEntities);
            } else {
                Log.d(TAG, "onPostExecute: Result is empty");
                Snackbar.make(getView(), "Result is empty", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class CreateProjectTask extends AsyncTask<Void, Void, Boolean> {

        AlertDialog dialog;
        ProjectsEntity projectsEntity;
        boolean successful = false;
        public CreateProjectTask(ProjectsEntity projectEntity) {
            this.projectsEntity = projectEntity;
            dialog = Utility.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Upload started");
            ParseObject project = new ParseObject(CloudClassNames.PROJECT.toString());
            project.put(PROJECT.PROJECT_CODE.toString(), projectsEntity.getProjectCode());
            project.put(PROJECT.PROJECT_TITLE.toString(), projectsEntity.getProjectName());
            project.put(PROJECT.PROJECT_OWNER.toString(), projectsEntity.getProjectOwner());
            project.put(PROJECT.SERVICES.toString(), projectsEntity.getProjectServices());
            project.put(PROJECT.TYPE.toString(), projectsEntity.getProjectType());
            project.put(PROJECT.SUBCATEGORY.toString(), projectsEntity.getProjectSubCategory());
            try {
                project.save();
                successful = true;
                Log.d(TAG, "doInBackground: project saved");
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Exception thrown: " + e.getMessage());
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            Log.d(TAG, "onPostExecute: Operation done");
            Log.d(TAG, "onPostExecute: result: " + String.valueOf(aBoolean));
            if(aBoolean) {
                Snackbar.make(getView(), "Record created successfully", Snackbar.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "onPostExecute: Operation failed");
                Snackbar.make(getView(), "Operation failed", Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
