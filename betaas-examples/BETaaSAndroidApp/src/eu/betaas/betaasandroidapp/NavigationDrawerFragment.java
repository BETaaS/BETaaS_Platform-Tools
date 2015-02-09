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

import java.util.ArrayList;
import java.util.List;

import eu.betaas.betaasandroidapp.gateway.GatewayListener;
import eu.betaas.betaasandroidapp.gateway.GatewayManager;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements GatewayListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private GatewayManager gatewayManager;
    
    private List<Gateway> gateways;
    private ArrayList<String> sidebarElements;
    
    private Context context;
    
    public NavigationDrawerFragment() {
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity().getApplicationContext();
        gatewayManager = GatewayManager.getInstance(context);
        gatewayManager.registerForAllEvents(this);
        
        gateways = gatewayManager.getGateways();
        sidebarElements = new ArrayList<String>();
        for (Gateway gateway : gateways) {
        	sidebarElements.add(gateway.getName());
        }
        
        sidebarElements.add(getString(R.string.add_gateway));
        
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 < sidebarElements.size() - 1) {
					showDialog(arg2);
				}
				return true;
			}
		});
        
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                //android.R.layout.simple_list_item_1,
                R.layout.navigation_drawer_item,
                R.id.navigationText,
                sidebarElements));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    @Override
	public void onDestroy() {
    	gatewayManager.unRegisterForAllEvents(this);
		super.onDestroy();
	}


	public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    	mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
        	if (position == sidebarElements.size()-1) {
        		mCallbacks.onNavigationDrawerItemSelected(null, true);
        	} else {
        		mCallbacks.onNavigationDrawerItemSelected(gateways.get(position), false);
        	}
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(Gateway gateway, boolean update);
    }

    private void showDialog(final int position) {
    	String[] actions = { getString(R.string.edit_gateway),
    						 getString(R.string.delete_gateway) };
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(sidebarElements.get(position))
		       .setItems(actions, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int which) {
		               Gateway gateway = gateways.get(position);
		               if (which == 0) {
			               mCallbacks.onNavigationDrawerItemSelected(gateway, true);
			               mDrawerLayout.closeDrawer(mFragmentContainerView);
			           } else {
			               gatewayManager.deleteGateway(gateway);
			               mDrawerLayout.closeDrawer(mFragmentContainerView);
			           }
		           }
		 });
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }

	@Override
	public void onGatewayInstallSuccess(Gateway gateway) {
		gateways.add(gateway);
		sidebarElements.add(sidebarElements.size() - 1, gateway.getName());
		selectItem(sidebarElements.size() - 2);
	}

	@Override
	public void onGatewayInstallFailure(Gateway gateway, String cause) {
		AlertDialog alertDialog =
				new AlertDialog.Builder(this.getActivity()).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage("[" + gateway.getName() + "] : " + cause);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}

	@Override
	public void onGatewayUpdateSuccess(Gateway gateway) {
		int position = 0;
		while (!gateways.get(position).getId().equals(gateway.getId())) {
			position++;
		}
		
		gateways.remove(position);
		gateways.add(position, gateway);
		sidebarElements.remove(position);
		sidebarElements.add(position, gateway.getName());
		selectItem(position);
	}

	@Override
	public void onGatewayUpdateFailure(Gateway gateway, String cause) {
		AlertDialog alertDialog =
				new AlertDialog.Builder(this.getActivity()).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage("[" + gateway.getName() + "] : " + cause);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}

	@Override
	public void onGatewayRemoveSuccess(Gateway gateway) {
		int position = sidebarElements.indexOf(gateway.getName());
		gateways.remove(position);
		sidebarElements.remove(position);
		selectItem(0);
	}

	@Override
	public void onGatewayRemoveFailure(Gateway gateway, String cause) {
		AlertDialog alertDialog =
				new AlertDialog.Builder(this.getActivity()).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage("[" + gateway.getName() + "] : " + cause);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}

	@Override
	public void onApplicationInstallSuccess(Gateway gateway) {}

	@Override
	public void onApplicationInstallFailure(String cause) {}

	@Override
	public void onApplicationUninstallSuccess() {}

	@Override
	public void onApplicationUninstallFailure() {}

	@Override
	public void onServiceSubscribeSuccess(String serviceId) {}

	@Override
	public void onServiceSubscribeFailure(String serviceId, String cause) {}

	@Override
	public void onServiceUnSubscribeSuccess() {}

	@Override
	public void onServiceUnSubscribeFailure() {}

	@Override
	public void onDataUpdate(Measurement measurement) {}
}
