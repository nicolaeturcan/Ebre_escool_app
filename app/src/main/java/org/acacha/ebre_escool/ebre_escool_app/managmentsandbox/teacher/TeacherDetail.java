package org.acacha.ebre_escool.ebre_escool_app.managmentsandbox.teacher;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.acacha.ebre_escool.ebre_escool_app.R;
import org.acacha.ebre_escool.ebre_escool_app.helpers.OnFragmentInteractionListener;
import org.acacha.ebre_escool.ebre_escool_app.managmentsandbox.teacher.api.TeacherApi;
import org.acacha.ebre_escool.ebre_escool_app.managmentsandbox.teacher.api.TeacherApiService;
import org.acacha.ebre_escool.ebre_escool_app.managmentsandbox.teacher.pojos.Result;
import org.acacha.ebre_escool.ebre_escool_app.managmentsandbox.teacher.pojos.Teacher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeacherDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;
    //Retrofit adapter
    private RestAdapter adapter;
    private Teacher teacherObject;
    private int teacherId;
    //Controls
    private TextView ID;
    private EditText personId;
    private EditText userId;
    private EditText entryDate;
    private EditText lastUpdate;
    private EditText lastUpdateUserId;
    private EditText creatorId;
    private EditText markedForDeletion;
    private EditText markedForDeletionDate;
    private EditText dniNif;
    private Button btnUpdate;
    private Button btnPut;
    private String TAG = "tag";
    private Calendar myCalendar;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeacherDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherDetail newInstance(String param1, String param2) {
        TeacherDetail fragment = new TeacherDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // View view= inflater.inflate(R.layout.fragment_teacher_detail, container, false);
        View view = inflater.inflate(R.layout.fragment_teacher_detail, container, false);
        //Get controls
        ID = (TextView) view.findViewById(R.id.teacherId);
        personId = (EditText) view.findViewById(R.id.personId);
        userId = (EditText) view.findViewById(R.id.userId);
        entryDate = (EditText) view.findViewById(R.id.entryDate);
        lastUpdate = (EditText) view.findViewById(R.id.lastUpdate);
        lastUpdateUserId = (EditText) view.findViewById(R.id.lastUpdateUserId);
        creatorId = (EditText) view.findViewById(R.id.creatorId);
        markedForDeletion = (EditText) view.findViewById(R.id.markedForDeletion);
        markedForDeletionDate = (EditText) view.findViewById(R.id.markedForDeletionDate);
        dniNif = (EditText) view.findViewById(R.id.dniNif);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        btnPut = (Button) view.findViewById(R.id.btnPut);
        //Set click listener for button update
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Teacher ID: "+ID.getText().toString(),Toast.LENGTH_LONG).show();
                updateTeacher();

            }
        });
        btnPut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Teacher ID: "+ID.getText().toString(),Toast.LENGTH_LONG).show();
                insertTeacher();

            }
        });
        //set rest adapter
        adapter = new RestAdapter.Builder()
                .setEndpoint(TeacherApi.ENDPOINT).build();

        // get data send from teacher fragment
        Bundle extras = getArguments();
        if (extras != null) {
            teacherId = extras.getInt("id");
            String action = extras.getString(TeacherApi.ACTION);
            Log.d("tag", "detail id :" + teacherId);
            switch (action) {
                case TeacherApi.DETAIL:
                    btnUpdate.setVisibility(View.INVISIBLE);
                    btnPut.setVisibility(View.INVISIBLE);
                    getOneTeacher(teacherId);
                    break;
                case TeacherApi.EDIT:
                    btnUpdate.setVisibility(View.VISIBLE);
                    btnPut.setVisibility(View.INVISIBLE);
                    getOneTeacher(teacherId);
                    break;
                case TeacherApi.PUT:
                    btnPut.setVisibility(View.VISIBLE);
                    btnUpdate.setVisibility(View.INVISIBLE);
                    markedForDeletion.setText("n");

            }
        }
        //open calendar when click on entry date edittext
         myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        entryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        return view;
    }
        //Set the new entry date
        private void updateLabel() {

            String myFormat = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
            entryDate.setText(sdf.format(myCalendar.getTime()));
        }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/
    //EXECUTE RETROFIT GET ONE TEACHER METHOD
    public void getOneTeacher(Integer id){
        //Show progress dialog
        progressDialog = ProgressDialog.show(getActivity(), "", "Loading Teacher data...", true);
        Log.d("tag","get :"+id);
        TeacherApiService api =adapter.create(TeacherApiService.class);
        api.getTeacher(id,new Callback<Teacher>() {
            @Override
            public void success(Teacher teacher, Response response) {
                // updateDisplay();
                Log.d("tag","success");
                teacherObject=teacher;
                updateDisplay();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("tag","failure");
            }
        });

    }
    //Update layout
    private void updateDisplay(){
        if(teacherObject==null){
            return;
        }
        //Set text on controls
        ID.setText(teacherObject.getId().toString());
        personId.setText(teacherObject.getPersonId());
        userId.setText(teacherObject.getUserId());
        entryDate.setText(teacherObject.getEntryDate());
        lastUpdate.setText(teacherObject.getLastUpdate());
        lastUpdateUserId.setText(teacherObject.getLastUpdateUserId());
        creatorId.setText(teacherObject.getCreatorId());
        markedForDeletion.setText(teacherObject.getMarkedForDeletion());
        markedForDeletionDate.setText(teacherObject.getMarkedForDeletionDate());
        dniNif.setText(teacherObject.getDNINIF());
         progressDialog.dismiss();

    }
    private Teacher getDataTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(ID.getText().toString());
        Log.d(TAG, "personid length: " + personId.getText().toString().length());
        Log.d(TAG, userId.getText().toString());
        Log.d(TAG,entryDate.getText().toString());
        Log.d(TAG, dniNif.getText().toString());



        //Check if fields are empty
        if(!(personId.getText().toString().length() ==0)){
            teacher.setPersonId(personId.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Some field is empty",Toast.LENGTH_LONG).show();
            return null;
        }

        if(!(userId.getText().toString().length()==0)) {
            teacher.setUserId(userId.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Some field is empty",Toast.LENGTH_LONG).show();
            return null;
        }
        if(!(entryDate.getText().toString().length()==0)) {

            teacher.setEntryDate(entryDate.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Some field is empty",Toast.LENGTH_LONG).show();
            return null;
        }
        //We dont need last update
        //teacher.setLastUpdate("");
        //can be null on the database
         teacher.setLastUpdateUserId(lastUpdateUserId.getText().toString());
         teacher.setCreatorId(creatorId.getText().toString());
        if(!(markedForDeletion.getText().toString().length()==0)) {
            teacher.setMarkedForDeletion(markedForDeletion.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Some field is empty",Toast.LENGTH_LONG).show();
            return null;
        }
       
            teacher.setMarkedForDeletionDate(markedForDeletionDate.getText().toString());

        if(!(dniNif.getText().toString().length()==0)) {
            teacher.setDNINIF(dniNif.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Some field is empty",Toast.LENGTH_LONG).show();
            return null;
        }
        //Return object teacher
        return teacher;
     }
    //Method to call retrofit post sending teacher to update
    private void updateTeacher(){
        //Get the teacher object
        Teacher teacher = getDataTeacher();

        //set rest adapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(TeacherApi.ENDPOINT).build();
        TeacherApiService api =adapter.create(TeacherApiService.class);

        api.updateTeacher(teacher,new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {
                Toast.makeText(getActivity(),"Teacher "+result.getId()+" "+result.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(),"UPDATE ERROR! "+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
     }

    //Method to put teacher
    private void insertTeacher() {
        Teacher teacher = getDataTeacher();
        if (!(teacher == null)){
            teacher.setId("");

        //Call put method
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(TeacherApi.ENDPOINT).build();
        TeacherApiService api = adapter.create(TeacherApiService.class);
        api.insertTeacher(teacher, new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {
                Toast.makeText(getActivity(), "Teacher " + result.getId() + " " + result.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "INSERT" +
                        "" +
                        " ERROR! " + error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
       }
    }


}
