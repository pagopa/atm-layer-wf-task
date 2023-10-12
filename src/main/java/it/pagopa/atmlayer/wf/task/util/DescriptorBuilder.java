package it.pagopa.atmlayer.wf.task.util;

import java.time.Instant;
import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.atmlayer.wf.task.bean.Channel;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Peripheral;
import it.pagopa.atmlayer.wf.task.bean.PeripheralStatus;
import it.pagopa.atmlayer.wf.task.bean.State;

public class DescriptorBuilder {
	
	public static void main(String[] args) throws JsonProcessingException {
		DescriptorBuilder builder = new DescriptorBuilder();
		builder.mainTask();
	}
	
	public void mainTask() throws JsonProcessingException {
		State state = new State();
		Device device = new Device();
		device.setBankId("02008");
		device.setBranchId("12345");
		device.setCode("0001");
		device.setChannel(Channel.ATM);
		device.setOpTimestamp(Instant.now());
		LinkedList<Peripheral> peripherals = new LinkedList<>();
		peripherals.addLast(new Peripheral("SCANNER", "QR Code scanner", PeripheralStatus.KO));
		peripherals.addLast(new Peripheral("PRINTER", "Receipt printer", PeripheralStatus.OK));
		device.setPeripherals(peripherals);
		state.setDevice(device);
		
		System.out.println("\n" + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(state));
	}
	

}
