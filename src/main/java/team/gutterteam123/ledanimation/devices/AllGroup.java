package team.gutterteam123.ledanimation.devices;


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AllGroup extends DeviceGroup {

    private static final long serialVersionUID = 1L;

    public AllGroup() {
        postDeserialization();
    }

    @Override
    public String displayName() {
        return "All";
    }

    @Override
    public void postDeserialization() {
        devices = new AbstractCollection<Device>() {
            //TODO use LevelIterable when upgrading
            @Override
            public Iterator<Device> iterator() {
                Collection<Device> devices = new ArrayList<>();
                for (Controllable controllable : FILE_SYSTEM.getEntries()) {
                    if (controllable instanceof Device) {
                        devices.add((Device) controllable);
                    }
                }
                return devices.iterator();
            }

            @Override
            public int size() {
                int count = 0;
                for (Controllable controllable : FILE_SYSTEM.getEntries()) {
                    if (controllable instanceof Device) {
                        count++;
                    }
                }
                return count;
            }
        };
    }

    @Override public void registerDevice(Device device) {}
    @Override public void unregisterDevice(Device device) {}
}
