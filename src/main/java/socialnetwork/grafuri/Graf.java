package socialnetwork.grafuri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graf {
    Map<Long, List<Long>> map;
    Map<Long,Boolean> vizitat;
    Map<Integer,List<Long>> comunitati;

    public Graf() {
        map = new HashMap<Long, List<Long>>();
    }

    public void addNod(Long id){
        map.put(id,new ArrayList<Long>());
    }

    public void removeNod(Long id){
        for(Map.Entry<Long,List<Long>> entry:map.entrySet() ){
            List<Long> list=entry.getValue();
            if(list.contains(id))
                list.remove(id);
            entry.setValue(list);
        }
        map.remove(id);
    }

    public void addMuchie(Long id1,Long id2){
        map.get(id1).add(id2);
        map.get(id2).add(id1);
    }

    public void removeMuchie(Long id1,Long id2){
        map.get(id1).remove(id2);
        map.get(id2).remove(id1);
    }

    @Override
    public String toString() {
        String rez="";
        for(Long i:map.keySet()) {
            rez+=i+": "+map.get(i).toString()+"\n";
        }
        return rez;
    }

    public String comunitatiToString() {
        String rez="";
        for(Integer i:comunitati.keySet()) {
            rez+=i+": "+comunitati.get(i).toString()+"\n";
        }
        return rez;
    }

    public Integer getcommunities(){
        vizitat=new HashMap<>();
        comunitati=new HashMap<>();
        Integer componente_conexe=0;

        for(Long v:map.keySet())
            vizitat.put(v,false);

        for(Long v: map.keySet())
            if(vizitat.get(v)==false){
                componente_conexe++;
                comunitati.put(componente_conexe,new ArrayList<Long>());
                DFS(v,componente_conexe);
            }
        return componente_conexe;
    }

    public void DFS(Long nod,Integer x){
        vizitat.replace(nod,true);
        comunitati.get(x).add(nod);
        for(Long adiacent:map.get(nod))
            if(vizitat.get(adiacent)==false) {
                DFS(adiacent, x);
            }
    }
}
