package com.jss.jssbackend.modules.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

public interface IAttachment extends Serializable {

	public List<Attachment> getAttachments();

}
