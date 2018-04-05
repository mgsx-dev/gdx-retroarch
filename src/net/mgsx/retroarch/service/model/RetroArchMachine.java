package net.mgsx.retroarch.service.model;

public class RetroArchMachine implements Comparable<RetroArchMachine> {

	public String name;

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(RetroArchMachine o) {
		return name.compareTo(o.name);
	}
}
