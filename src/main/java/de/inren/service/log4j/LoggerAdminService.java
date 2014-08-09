package de.inren.service.log4j;

import java.util.List;

import org.apache.log4j.Logger;

import de.inren.data.domain.loggerbean.LoggerBean;
import de.inren.service.Initializable;


public interface LoggerAdminService extends Initializable {
	
	
	/**
	 * Get list of references for all logger, available by this service.
	 * 
	 * @return List of Loggers never null
	 */
	List<LoggerBean> getAllLogger();

	void reInit();

	List<String> getLogLevels();

	void setLevel(Logger logger, String level);

	void setLevelAndSave(String logger, String level);
}
