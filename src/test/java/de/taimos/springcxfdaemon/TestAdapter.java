package de.taimos.springcxfdaemon;

public class TestAdapter extends SpringDaemonAdapter {
	
	private String res;
	
	
	/**
	 * @param res
	 */
	public TestAdapter(String res) {
		this.res = res;
	}
	
	@Override
	protected String getSpringResource() {
		return "spring/" + this.res + ".xml";
	}
	
}
