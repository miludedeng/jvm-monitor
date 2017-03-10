package cc.cafetime.type;

/**
 * ApplicationType which has custom name and icon.
 * Instance of this class is constructed by {@link MainClassApplicationTypeFactory}
 * @author Tomas Hurka
 */
public class MainClassApplicationType  {
  String name;
  String description;
  String icon;


  MainClassApplicationType(String name, String description, String icon) {
    this.name = name;
    this.description = description;
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description != null ? description : ""; // NOI18N
  }

  public String getIcon(){
    return icon;
  }

}
