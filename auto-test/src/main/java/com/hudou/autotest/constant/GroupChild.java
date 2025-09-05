package com.hudou.autotest.constant;

import androidx.annotation.IntDef;

import com.hudou.autotest.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class GroupChild {
    private final int groupId;
    private final int childId;
    private final String titleName;


    public GroupChild(@Group int groupId, @Child int childId, String titleName) {
        this.groupId = groupId;
        this.childId = childId;
        this.titleName = titleName;
    }

    @IntDef({Group.TEST_REPORT, Group.LOAD_FILES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Group {
        int TEST_REPORT = 0;
        int LOAD_FILES = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Child {

    }

    @IntDef()
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReportChild {

    }


    public static final int[][] child = new int[][]{
            {
                    R.string.group_test_report,
                    R.string.child_report_path,
                    R.string.child_output_xlsx_report,
                    R.string.child_recording,
                    R.string.child_view_report_name,
                    R.string.child_clean_records,
            },
            {
                    R.string.group_load_files,
                    R.string.child_files_path,
                    R.string.child_load_files,
            }
    };

    public static final List<List<Integer>> groupChildMap = new ArrayList<List<Integer>>() {{
        add(R.string.group_test_report, new ArrayList<Integer>() {{
            add(R.string.child_report_path);
            add(R.string.child_output_xlsx_report);
            add(R.string.child_recording);
            add(R.string.child_view_report_name);
            add(R.string.child_clean_records);
        }});
        add(R.string.group_load_files, new ArrayList<Integer>() {{
            add(R.string.child_files_path);
            add(R.string.child_load_files);
        }});
    }};


}
