package lab.nicc.kioskyoungcheon;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by NG1 on 2017-08-02.
 */

public class OrganizationPagerAdapter extends FragmentStatePagerAdapter {

    //public int teamNum = new JsonParser().getTeamCount() - 1;
    public float pageWidth = .33333333333f;

    public int teamNum;

    public OrganizationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setTeamNum(int teamNum){
        this.teamNum = teamNum;
    }

    @Override
    public Fragment getItem(int position) {
        return OrganizationFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return teamNum;
    }

    @Override
    public float getPageWidth(int position) {
        return pageWidth;
    }

    public void setPageWidth(float pageWidth){ this.pageWidth = pageWidth; }
}