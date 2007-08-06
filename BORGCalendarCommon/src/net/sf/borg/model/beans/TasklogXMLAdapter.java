// This code was generated by GenerateDataObjects
package net.sf.borg.model.beans;

import net.sf.borg.common.XTree;
public class TasklogXMLAdapter extends BeanXMLAdapter {

	public XTree toXml( KeyedBean b )
	{
		Tasklog o = (Tasklog) b;
		XTree xt = new XTree();
		xt.name("Tasklog");
		xt.appendChild("KEY", Integer.toString(o.getKey()));
		if( o.getId() != null )
			xt.appendChild("Id", BeanXMLAdapter.toString(o.getId()));
		if( o.getlogTime() != null )
			xt.appendChild("logTime", BeanXMLAdapter.toString(o.getlogTime()));
		if( o.getDescription() != null && !o.getDescription().equals(""))
			xt.appendChild("Description", o.getDescription());
		if( o.getTask() != null )
			xt.appendChild("Task", BeanXMLAdapter.toString(o.getTask()));
		return( xt );
	}

	public KeyedBean fromXml( XTree xt )
	{
		Tasklog ret = new Tasklog();
		String ks = xt.child("KEY").value();
		ret.setKey( BeanXMLAdapter.toInt(ks) );
		String val = "";
		val = xt.child("Id").value();
		ret.setId( BeanXMLAdapter.toInteger(val) );
		val = xt.child("logTime").value();
		ret.setlogTime( BeanXMLAdapter.toDate(val) );
		val = xt.child("Description").value();
		if( !val.equals("") )
			ret.setDescription( val );
		val = xt.child("Task").value();
		ret.setTask( BeanXMLAdapter.toInteger(val) );
		return( ret );
	}
}
