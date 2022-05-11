package fr.sedoo.openopse.rest.dao;

public interface DataDao {

	void prepare(String uuid, String filter, String requestId);

	String check(String requestid);

	byte[] downloadGeneratedFile(String requestid, String zipFileName) throws Exception;

}
