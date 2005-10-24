/*
 * pojos.Experimenter
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package pojos;

import ome.api.OMEModel;
import ome.model.Experimenter;
import ome.model.Group;
import ome.util.ModelMapper;


//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * The data that makes up an <i>OME</i> Experimenter along with information
 * about the Group the Experimenter belongs in.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision: $ $Date: $)
 * </small>
 * @since OME2.2
 */
public class ExperimenterData
    implements DataObject
{

    /** The Experimenter ID. */
    private int         id;
    
    /** The Experimenter's first name. */
    private String      firstName;
    
    /** The Experimenter's last name. */
    private String      lastName;
    
    /** The Experimenter's email. */
    private String      email;
    
    /** The Experimenter's institution. */
    private String      institution;
    
    /** The ID of the Group this Experimenter belongs in. */
    private int         groupID;
    
    /** The name of the Group this Experimenter belongs in. */
    private String      groupName;
     
    public void copy(OMEModel model, ModelMapper mapper) {
    	if (model instanceof Experimenter) {
			Experimenter exp = (Experimenter) model;
			if (exp.getAttributeId()!=null){
				this.setId(exp.getAttributeId().intValue());
			}
			this.setFirstName(exp.getFirstname());
			this.setLastName(exp.getLastname());
			this.setEmail(exp.getEmail());
			this.setInstitution(exp.getInstitution());
			if (exp.getGroup()!=null){
				Group g = exp.getGroup();
				this.setGroupID(mapper.nullSafeInt(g.getAttributeId()));
				this.setGroupName(g.getName());
			}
		} else {
			throw new IllegalArgumentException("ExperimenterData can only copy from Experimenter");
		}
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getInstitution() {
		return institution;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
}
