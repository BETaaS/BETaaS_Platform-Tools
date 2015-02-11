/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp;

import eu.betaas.betaasandroidapp.configuration.Configuration;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle= getTitle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Configuration configuration = Configuration.getInstance();
		if (configuration.getIP() == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setMessage(R.string.no_internet)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(-1);
                }
            });
			builder.create().show();
		}
		
		//mTitle = getTitle();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
			.findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
			(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(Gateway element, boolean update) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if (element == null) {
			fragmentManager
					.beginTransaction()
					.replace(R.id.container,
							new ModifyGatewayFragment()).commit();
			mTitle = getString(R.string.add_gateway);
			restoreActionBar();
		} else {
			if (update) {
				fragment = new ModifyGatewayFragment();
				Bundle args = new Bundle();
				args.putString("id", element.getId());
				fragment.setArguments(args);
				
				mTitle = getString(R.string.edit_gateway) + " " + element.getName();
			} else {
				fragment = new GatewayFragment();
				Bundle args = new Bundle();
				args.putString("id", element.getId());
				fragment.setArguments(args);
				
				mTitle = element.getName();
			}
			
			fragmentManager
				.beginTransaction()
				.replace(R.id.container, fragment).commit();
			
			restoreActionBar();
		}
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
	
	public void onAddedGateway(Gateway gateway) {
		
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			/*((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));*/
		}
	}
}
