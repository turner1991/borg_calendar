// This code was generated by GenerateDataObjects
package net.sf.borg.model.db.file;

import net.sf.borg.model.beans.KeyedBean;
import net.sf.borg.model.beans.Task;
import net.sf.borg.model.db.file.mdb.Row;
import net.sf.borg.model.db.file.mdb.Schema;
public class TaskAdapter implements FileBeanAdapter {

	public KeyedBean fromRow( Row r ) throws Exception
	{
		Task ret = new Task();
		ret.setKey(r.getKey());
		ret.setTaskNumber( r.getInteger("N"));
		ret.setStartDate( r.getDate("SD"));
		ret.setCD( r.getDate("CD"));
		ret.setDueDate( r.getDate("DD"));
		ret.setET( r.getDate("ET"));
		ret.setPersonAssigned( r.getString("PA"));
		ret.setPriority( r.getInteger("PR"));
		ret.setState( r.getString("ST"));
		ret.setType( r.getString("TY"));
		ret.setDescription( r.getString("DE"));
		ret.setResolution( r.getString("RE"));
		ret.setTodoList( r.getString("TD"));
		ret.setUserTask1( r.getString("UT1"));
		ret.setUserTask2( r.getString("UT2"));
		ret.setUserTask3( r.getString("UT3"));
		ret.setUserTask4( r.getString("UT4"));
		ret.setUserTask5( r.getString("UT5"));
		ret.setCategory( r.getString("CAT"));
		ret.setProject( r.getInteger("PROJ"));
		return( ret );
	}

	public Row toRow( Schema sch, KeyedBean b, boolean normalize ) throws Exception
	{
		Task o = (Task) b;
		Row ret = new Row(sch);
		ret.normalize(normalize);
		ret.setKey(o.getKey());
		ret.setField("N", o.getTaskNumber());
		ret.setField("SD", o.getStartDate());
		ret.setField("CD", o.getCD());
		ret.setField("DD", o.getDueDate());
		ret.setField("ET", o.getET());
		ret.setField("PA", o.getPersonAssigned());
		ret.setField("PR", o.getPriority());
		ret.setField("ST", o.getState());
		ret.setField("TY", o.getType());
		ret.setField("DE", o.getDescription());
		ret.setField("RE", o.getResolution());
		ret.setField("TD", o.getTodoList());
		ret.setField("UT1", o.getUserTask1());
		ret.setField("UT2", o.getUserTask2());
		ret.setField("UT3", o.getUserTask3());
		ret.setField("UT4", o.getUserTask4());
		ret.setField("UT5", o.getUserTask5());
		ret.setField("CAT", o.getCategory());
		ret.setField("PROJ", o.getProject());
		return( ret );
	}

	public KeyedBean newBean() {
		return( new Task());
	}
}
