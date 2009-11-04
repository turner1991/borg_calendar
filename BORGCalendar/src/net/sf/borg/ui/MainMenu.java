/*

 BORG is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 BORG is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with BORG; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Copyright 2006 by Mike Berger
 */
package net.sf.borg.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.sf.borg.common.Errmsg;
import net.sf.borg.common.IOHelper;
import net.sf.borg.common.PrefName;
import net.sf.borg.common.Prefs;
import net.sf.borg.common.Resource;
import net.sf.borg.control.Borg;
import net.sf.borg.model.AddressModel;
import net.sf.borg.model.AppointmentModel;
import net.sf.borg.model.CategoryModel;
import net.sf.borg.model.LinkModel;
import net.sf.borg.model.MemoModel;
import net.sf.borg.model.TaskModel;
import net.sf.borg.model.db.jdbc.JdbcDB;
import net.sf.borg.model.entity.Appointment;
import net.sf.borg.model.entity.Task;
import net.sf.borg.model.undo.UndoLog;
import net.sf.borg.ui.task.TaskConfigurator;
import net.sf.borg.ui.util.InputDialog;
import net.sf.borg.ui.util.ScrolledDialog;

// TODO - javadoc not really done
/**
 * The borg main menu bar
 * 
 */
class MainMenu {
	private JMenuItem AboutMI = new JMenuItem();
	private JMenu ActionMenu = new JMenu();
	private JMenuItem addCategoryMI = new JMenuItem();
	private JMenu catmenu = new JMenu();
	private JMenuItem chooseCategoriesMI = new JMenuItem();
	private JMenuItem dbMI = new JMenuItem();
	private JMenuItem delcatMI;
	private JMenuItem editPrefsMenuItem = new JMenuItem();
	private JMenuItem exitMenuItem = new JMenuItem();
	private JMenuItem exportMI = new JMenuItem();
	private JMenu expXML = new JMenu();
	private JMenu helpmenu = new JMenu();
	private JMenuItem helpMI = new JMenuItem();
	private JMenu impexpMenu = new JMenu();
	private JMenuItem importMI = new JMenuItem();
	private JMenu impXML = new JMenu();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu OptionMenu = new JMenu();
	private JMenuItem removeCategoryMI = new JMenuItem();
	private JMenuItem sqlMI = new JMenuItem();
	private JMenuItem syncMI = new JMenuItem();

	/**
	 * constructor
	 */
	public MainMenu() {

		menuBar.setBorder(new javax.swing.border.BevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		ActionMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Application16.gif")));
		ResourceHelper.setText(ActionMenu, "Action");

		

		syncMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Refresh16.gif")));
		syncMI.setText(Resource.getResourceString( "Synchronize"));
		syncMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					Borg.syncDBs();
				} catch (Exception e) {
					Errmsg.errmsg(e);
				}
			}
		});

		ActionMenu.add(syncMI);

		sqlMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Refresh16.gif")));
		ResourceHelper.setText(sqlMI, "RunSQL");
		sqlMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new SqlRunner().setVisible(true);
			}
		});

		ActionMenu.add(sqlMI);

		exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Stop16.gif")));
		exitMenuItem.setText(Resource.getResourceString( "Exit"));
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Borg.shutdown();
			}
		});

		ActionMenu.add(exitMenuItem);

		menuBar.add(ActionMenu);

		OptionMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Preferences16.gif")));
		ResourceHelper.setText(OptionMenu, "Options");

		ResourceHelper.setText(editPrefsMenuItem, "ep");
		editPrefsMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						OptionsView.getReference().setVisible(true);
					}
				});
		OptionMenu.add(editPrefsMenuItem);

		JMenuItem exportPrefsMI = new JMenuItem();
		exportPrefsMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Export16.gif")));
		ResourceHelper.setText(exportPrefsMI, "export_prefs");
		exportPrefsMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				expPrefs();
			}
		});
		OptionMenu.add(exportPrefsMI);

		JMenuItem mportPrefsMI = new JMenuItem();
		mportPrefsMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Import16.gif")));
		ResourceHelper.setText(mportPrefsMI, "import_prefs");
		mportPrefsMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				impPrefs();
			}
		});
		OptionMenu.add(mportPrefsMI);

		JMenu tsm = new JMenu(Resource.getResourceString("task_state_options"));
		JMenuItem edittypes = new JMenuItem();
		JMenuItem resetst = new JMenuItem();
		ResourceHelper.setText(edittypes, "edit_types");
		edittypes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					TaskConfigurator.getReference().setVisible(true);
				} catch (Exception e) {
					Errmsg.errmsg(e);
				}
			}
		});

		tsm.add(edittypes);

		ResourceHelper.setText(resetst, "Reset_Task_States_to_Default");
		resetst.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetstActionPerformed();
			}
		});

		tsm.add(resetst);
		OptionMenu.add(tsm);
		menuBar.add(OptionMenu);

		catmenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Preferences16.gif")));
		ResourceHelper.setText(catmenu, "Categories");
		chooseCategoriesMI.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/resource/Preferences16.gif")));
		ResourceHelper.setText(chooseCategoriesMI, "choosecat");
		chooseCategoriesMI.setActionCommand("Choose Displayed Categories");
		chooseCategoriesMI
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						CategoryChooser.getReference().setVisible(true);
					}
				});

		catmenu.add(chooseCategoriesMI);

		addCategoryMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Add16.gif")));
		ResourceHelper.setText(addCategoryMI, "addcat");
		addCategoryMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String inputValue = InputDialog.show(Resource
						.getResourceString("AddCat"), 15);
				if (inputValue == null || inputValue.equals(""))
					return;
				try {
					CategoryModel.getReference().addCategory(inputValue);
					CategoryModel.getReference().showCategory(inputValue);
				} catch (Exception e) {
					Errmsg.errmsg(e);
				}
			}
		});

		catmenu.add(addCategoryMI);

		removeCategoryMI.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/resource/Delete16.gif")));
		ResourceHelper.setText(removeCategoryMI, "remcat");
		removeCategoryMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					CategoryModel.getReference().syncCategories();
				} catch (Exception e) {
					Errmsg.errmsg(e);
				}
			}
		});

		catmenu.add(removeCategoryMI);

		menuBar.add(catmenu);

		impexpMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Export16.gif")));
		ResourceHelper.setText(impexpMenu, "impexpMenu");
		impXML.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Import16.gif")));
		ResourceHelper.setText(impXML, "impXML");
		ResourceHelper.setText(importMI, "impmenu");
		importMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				importMIActionPerformed();
			}
		});

		impXML.add(importMI);

		impexpMenu.add(impXML);

		expXML.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Export16.gif")));
		ResourceHelper.setText(expXML, "expXML");
		ResourceHelper.setText(exportMI, "expmenu");
		exportMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportMIActionPerformed();
			}
		});

		expXML.add(exportMI);

		impexpMenu.add(expXML);

		menuBar.add(impexpMenu);

		String dbtype = Prefs.getPref(PrefName.DBTYPE);

		menuBar.add(getUndoMenu());

		JMenu reportMenu = RunReport.getReportMenu();
		if( reportMenu != null )
			menuBar.add(reportMenu);
		menuBar.add(Box.createHorizontalGlue());

		helpmenu.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Help16.gif")));
		ResourceHelper.setText(helpmenu, "Help");
		ResourceHelper.setText(helpMI, "Help");
		helpMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					HelpLauncher.launchHelp();
				} catch (Exception e) {
					Errmsg.errmsg(e);
				}
			}
		});

		helpmenu.add(helpMI);

		ResourceHelper.setText(dbMI, "DatabaseInformation");
		dbMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dbMIActionPerformed();
			}
		});

		helpmenu.add(dbMI);

		ResourceHelper.setText(AboutMI, "About");
		AboutMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AboutMIActionPerformed();
			}
		});

		helpmenu.add(AboutMI);

		menuBar.add(helpmenu);
		catmenu.add(getDelcatMI());

		if (dbtype.equals("mysql") || dbtype.equals("jdbc")) {
			syncMI.setEnabled(true);
		} else {
			syncMI.setEnabled(false);
		}

		importMI.setEnabled(true);
		exportMI.setEnabled(true);

	}

	/**
	 * Add an action to the action menu
	 * @param icon the icon for the menu item
	 * @param text the text for the menu item
	 * @param action the action listener for the menu item
	 */
	public void addAction(Icon icon, String text, ActionListener action) {
		JMenuItem item = new JMenuItem();
		item.setIcon(icon);
		item.setText(text);
		item.addActionListener(action);

		ActionMenu.insert(item, lastActionInsert++);
	}
	private int lastActionInsert = 0;

	/**
	 * add an item to the help menu
	 * @param icon the icon for the menu item
	 * @param text the menu item text
	 * @param action the menu item action 
	 */
	public void addHelpMenuItem(Icon icon, String text, ActionListener action)
	{
		JMenuItem item = new JMenuItem();
		item.setIcon(icon);
		item.setText(text);
		item.addActionListener(action);

		helpmenu.add(item);
	}
	

	/**
	 * show the about window
	 * 
	 */
	private void AboutMIActionPerformed() {

		// show the About data
		String build_info = "";
		String version = "";
		try {
			// get the version and build info from a properties file in the
			// jar
			// file
			InputStream is = getClass().getResource("/properties").openStream();
			Properties props = new Properties();
			props.load(is);
			is.close();
			version = Resource.getVersion();
			build_info = Resource.getResourceString("Build_Number:_")
					+ props.getProperty("build.number")
					+ Resource.getResourceString("Build_Time:_")
					+ props.getProperty("build.time");

		} catch (Exception e) {
			Errmsg.errmsg(e);
		}

		// build and show the version info.

		String info = Resource.getResourceString("Berger-Organizer_v")
				+ version
				+ "\n"
				+ Resource.getResourceString("copyright")
				+ " (2003-2009) Michael Berger <i_flem@users.sourceforge.net>\nhttp://borg-calendar.sourceforge.net\n\n"
				+ Resource.getResourceString("contributions_by") + "\n"
				+ Resource.getResourceString("contrib") + "\n"
				+ Resource.getResourceString("translations") + "\n\n"
				+ build_info + "\n" + "Java "
				+ System.getProperty("java.version");
		Object opts[] = { Resource.getResourceString("Dismiss") };
		JOptionPane.showOptionDialog(null, info, Resource
				.getResourceString("About_BORG"), JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass()
						.getResource("/resource/borg.jpg")), opts, opts[0]);

	}

	/** show database info */
	private void dbMIActionPerformed() {
		String dbtype = Prefs.getPref(PrefName.DBTYPE);
		String info = Resource.getResourceString("DatabaseInformation")
				+ ":\n\n";
		info += dbtype + " URL: " + JdbcDB.getUrl() + "\n\n";

		try {
			info += Resource.getResourceString("appointments") + ": "
					+ AppointmentModel.getReference().getAllAppts().size()
					+ "\n";
			info += Resource.getResourceString("addresses") + ": "
					+ AddressModel.getReference().getAddresses().size() + "\n";
			info += Resource.getResourceString("tasks") + ": "
					+ TaskModel.getReference().getTasks().size() + "\n";
			info += Resource.getResourceString("SubTasks") + ": "
					+ TaskModel.getReference().getSubTasks().size() + "\n";
			info += Resource.getResourceString("Logs") + ": "
					+ TaskModel.getReference().getLogs().size() + "\n";
			info += Resource.getResourceString("projects") + ": "
					+ TaskModel.getReference().getProjects().size() + "\n";
			info += Resource.getResourceString("Memos") + ": "
					+ MemoModel.getReference().getMemos().size() + "\n";
			info += Resource.getResourceString("links") + ": "
					+ LinkModel.getReference().getLinks().size() + "\n";
		} catch (Exception e) {
			Errmsg.errmsg(e);
			return;
		}

		ScrolledDialog.showNotice(info);
	}

	/** export */
	private void exportMIActionPerformed() {

		// user wants to export the task and calendar DBs to an XML file
		File dir;
		while (true) {
			// prompt for a directory to store the files
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser
					.setDialogTitle(Resource
							.getResourceString("Please_choose_directory_to_place_XML_files"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setApproveButtonText(Resource
					.getResourceString("select_export_dir"));

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			String s = chooser.getSelectedFile().getAbsolutePath();
			dir = new File(s);
			String err = null;
			if (!dir.exists()) {
				err = Resource.getResourceString("Directory_[") + s
						+ Resource.getResourceString("]_does_not_exist");
			} else if (!dir.isDirectory()) {
				err = "[" + s
						+ Resource.getResourceString("]_is_not_a_directory");
			}

			if (err == null)
				break;

			Errmsg.notice(err);
		}

		try {

			JOptionPane.showMessageDialog(null, Resource
					.getResourceString("export_notice")
					+ dir.getAbsolutePath());

			String fname = dir.getAbsolutePath() + "/borg.xml";
			if (IOHelper.checkOverwrite(fname)) {
				OutputStream ostr = IOHelper.createOutputStream(fname);
				Writer fw = new OutputStreamWriter(ostr, "UTF8");
				AppointmentModel.getReference().export(fw);
				fw.close();
			}

			fname = dir.getAbsolutePath() + "/mrdb.xml";
			if (IOHelper.checkOverwrite(fname)) {
				OutputStream ostr = IOHelper.createOutputStream(fname);
				Writer fw = new OutputStreamWriter(ostr, "UTF8");
				TaskModel.getReference().export(fw);
				fw.close();
			}

			fname = dir.getAbsolutePath() + "/addr.xml";
			if (IOHelper.checkOverwrite(fname)) {
				OutputStream ostr = IOHelper.createOutputStream(fname);
				Writer fw = new OutputStreamWriter(ostr, "UTF8");
				AddressModel.getReference().export(fw);
				fw.close();
			}

			fname = dir.getAbsolutePath() + "/memo.xml";
			if (IOHelper.checkOverwrite(fname)) {
				OutputStream ostr = IOHelper.createOutputStream(fname);
				Writer fw = new OutputStreamWriter(ostr, "UTF8");
				MemoModel.getReference().export(fw);
				fw.close();
			}

			fname = dir.getAbsolutePath() + "/link.xml";
			if (IOHelper.checkOverwrite(fname)) {
				OutputStream ostr = IOHelper.createOutputStream(fname);
				Writer fw = new OutputStreamWriter(ostr, "UTF8");
				LinkModel.getReference().export(fw);
				fw.close();
			}

		} catch (Exception e) {
			Errmsg.errmsg(e);
		}
	}

	/** export preferences to an XML file */
	private void expPrefs() {
		File file;
		while (true) {
			// prompt for a file
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle(Resource.getResourceString("choose_file"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			String s = chooser.getSelectedFile().getAbsolutePath();
			file = new File(s);

			break;

		}

		try {
			Prefs.export(file.getAbsolutePath());
		} catch (Exception e) {
			Errmsg.errmsg(e);
		}

	}

	/**
	 * delete category menu item
	 */
	private JMenuItem getDelcatMI() {
		if (delcatMI == null) {
			delcatMI = new JMenuItem();
			ResourceHelper.setText(delcatMI, "delete_cat");
			delcatMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
					"/resource/Delete16.gif")));
			delcatMI.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					try {

						// get category list
						CategoryModel catmod = CategoryModel.getReference();
						Collection<String> allcats = catmod.getCategories();
						allcats.remove(CategoryModel.UNCATEGORIZED);
						if (allcats.isEmpty())
							return;
						Object[] cats = allcats.toArray();

						// ask user to choose a category
						Object o = JOptionPane.showInputDialog(null, Resource
								.getResourceString("delete_cat_choose"), "",
								JOptionPane.QUESTION_MESSAGE, null, cats,
								cats[0]);
						if (o == null)
							return;

						// confirm with user
						int ret = JOptionPane.showConfirmDialog(null, Resource
								.getResourceString("delcat_warn")
								+ " [" + (String) o + "]!", "",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE);
						if (ret == JOptionPane.OK_OPTION) {

							// deletes all appts and tasks with that cetegory
							// !!!!!

							// appts
							Iterator<?> itr = AppointmentModel.getReference()
									.getAllAppts().iterator();
							while (itr.hasNext()) {
								Appointment ap = (Appointment) itr.next();
								String cat = ap.getCategory();
								if (cat != null && cat.equals(o))
									AppointmentModel.getReference().delAppt(ap);
							}

							// tasks
							itr = TaskModel.getReference().getTasks()
									.iterator();
							while (itr.hasNext()) {
								Task t = (Task) itr.next();
								String cat = t.getCategory();
								if (cat != null && cat.equals(o))
									TaskModel.getReference().delete(t.getKey());
							}

							try {
								CategoryModel.getReference().syncCategories();
							} catch (Exception ex) {
								Errmsg.errmsg(ex);
							}
						}
					} catch (Exception ex) {
						Errmsg.errmsg(ex);
					}
				}
			});
		}
		return delcatMI;
	}

	/**
	 * get the menu bar
	 * 
	 * @return the menu bar
	 */
	public JMenuBar getMenuBar() {

		return menuBar;
	}

	/** report menu */
	

	/** undo menu */
	private JMenu getUndoMenu() {
		JMenu m = new JMenu();
		m.setText(Resource.getResourceString("undo"));
		m.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Refresh16.gif")));
		final JMenu menu = m;
		m.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent e) {
				menu.removeAll();

				final String top = UndoLog.getReference().getTopItem();
				if (top != null) {
					JMenuItem mi = new JMenuItem(Resource
							.getResourceString("undo")
							+ ": " + top);
					mi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							int ret = JOptionPane
									.showConfirmDialog(
											null,
											Resource.getResourceString("undo")
													+ ": "
													+ top
													+ "\n\n"
													+ Resource
															.getResourceString("please_confirm"),
											"", JOptionPane.OK_CANCEL_OPTION);
							if (ret != JOptionPane.OK_OPTION)
								return;
							UndoLog.getReference().executeUndo();
						}

					});
					menu.add(mi);
					JMenuItem cmi = new JMenuItem();
					cmi.setText(Resource.getResourceString("clear_undos"));
					cmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							int ret = JOptionPane
									.showConfirmDialog(
											null,
											Resource
													.getResourceString("clear_undos")
													+ "\n\n"
													+ Resource
															.getResourceString("please_confirm"),
											"", JOptionPane.OK_CANCEL_OPTION);
							if (ret != JOptionPane.OK_OPTION)
								return;
							UndoLog.getReference().clear();
						}

					});
					menu.add(cmi);

					boolean show_stack = Prefs
							.getBoolPref(PrefName.SHOW_UNDO_STACK);
					if (show_stack == true) {
						JMenu all_mi = new JMenu(Resource
								.getResourceString("all_undos"));
						for (String item : UndoLog.getReference()
								.getItemStrings()) {
							JMenuItem item_mi = new JMenuItem(Resource
									.getResourceString("undo")
									+ ": " + item);
							all_mi.add(item_mi);
						}

						menu.add(all_mi);
					}
				} else {
					menu.add(new JMenuItem(Resource
							.getResourceString("no_undos")));
				}

			}

		});

		return m;
	}

	/** import from file */
	private void importMIActionPerformed() {
		try {

			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle(Resource
					.getResourceString("Please_choose_File_to_Import_From"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			String fileName = chooser.getSelectedFile().getAbsolutePath();
			BufferedReader in = new BufferedReader(new FileReader(new File(
					fileName)));

			String type = "";
			for (int i = 0; i < 10; i++) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.contains("<ADDRESSES>")) {
					type = "ADDRESSES";
					break;
				} else if (line.contains("<MEMOS>")) {
					type = "MEMOS";
					break;
				} else if (line.contains("<LINKS>")) {
					type = "LINKS";
					break;
				} else if (line.contains("<TASKS>")) {
					type = "TASKS";
					break;
				} else if (line.contains("<APPTS>")) {
					type = "APPTS";
					break;
				}
			}

			in.close();

			int ret = JOptionPane.showConfirmDialog(null, Resource
					.getResourceString("Importing_")
					+ " " + type + ", OK?", Resource
					.getResourceString("Import_WARNING"),
					JOptionPane.OK_CANCEL_OPTION);

			if (ret != JOptionPane.OK_OPTION)
				return;

			if (type.equals("ADDRESSES")) {
				AddressModel.getReference().importXml(fileName);
			} else if (type.equals("LINKS")) {
				LinkModel.getReference().importXml(fileName);
			} else if (type.equals("MEMOS")) {
				MemoModel.getReference().importXml(fileName);
			} else if (type.equals("TASKS")) {
				TaskModel.getReference().importXml(fileName);
			} else if (type.equals("APPTS")) {
				AppointmentModel.getReference().importXml(fileName);
			}

			// show any newly imported categories
			CategoryModel.getReference().syncCategories();
			CategoryModel.getReference().showAll();
		} catch (Exception e) {
			Errmsg.errmsg(e);
		}
	}

	/** import preferences */
	private void impPrefs() {
		File file;
		while (true) {
			// prompt for a file
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle(Resource.getResourceString("choose_file"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			String s = chooser.getSelectedFile().getAbsolutePath();
			file = new File(s);

			break;

		}

		try {
			Prefs.importPrefs(file.getAbsolutePath());
		} catch (Exception e) {
			Errmsg.errmsg(e);
		}

	}

	/**
	 * reset task tate action
	 */
	private void resetstActionPerformed() {
		try {
			String msg = Resource.getResourceString("reset_state_warning");
			int ret = JOptionPane.showConfirmDialog(null, msg, Resource
					.getResourceString("Import_WARNING"),
					JOptionPane.OK_CANCEL_OPTION);

			if (ret != JOptionPane.OK_OPTION)
				return;
			TaskModel taskmod_ = TaskModel.getReference();
			taskmod_.getTaskTypes().loadDefault();
		} catch (Exception e) {
			Errmsg.errmsg(e);
		}
	}

}
