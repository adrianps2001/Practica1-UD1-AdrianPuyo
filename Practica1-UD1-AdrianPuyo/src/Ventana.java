
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import base.Movil;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Ventana {
    private JComboBox comboBox;
    private JButton altaMovilBtn;
    private JButton mostrarMovilBtn;
    private JTextField marcaTxt;
    private JTextField modeloTxt;
    private JLabel lblMovil;
    private JPanel panel1;
    private JButton limpiarMovilBtn;
    private JButton bajaMovilBtn;
    private JFrame frame;


    private LinkedList<Movil> lista;
    private DefaultComboBoxModel<Movil> dcbm;

    public Ventana(){
        frame = new JFrame("Ventana");

        frame.setContentPane(panel1);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        crearMenu();
        frame.setLocationRelativeTo(null);
        lista = new LinkedList<>();
        dcbm = new DefaultComboBoxModel<>();
        comboBox.setModel(dcbm);

        altaMovilBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                altaMovil(marcaTxt.getText(), modeloTxt.getText());
                refrescarComboBox();
            }
        });
        mostrarMovilBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Movil seleccionado = (Movil) dcbm.getSelectedItem();
                    lblMovil.setText(seleccionado.toString());
                }catch (NullPointerException q){
                    lblMovil.setText("La lista esta vacia");
                }
                }
        });
        limpiarMovilBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lista.clear();
                refrescarComboBox();
            }
        });
        bajaMovilBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Movil seleccionado = (Movil) dcbm.getSelectedItem();
                lista.remove(seleccionado);
                refrescarComboBox();
            }
        });
    }
    private void refrescarComboBox() {
        dcbm.removeAllElements();
        for (Movil movil : lista) {
            dcbm.addElement(movil);
        }
    }
    private void altaMovil(String marca, String modelo) {
        lista.add(new Movil(marca, modelo));

    }
    public static void main(String[] args) {
        Ventana ventana = new Ventana();

    }

    public void crearMenu(){
        JMenuBar barra = new JMenuBar();
        JMenu menu = new JMenu("Archivo");
        JMenuItem itemExportarXML = new JMenuItem("Exportar XML");
        JMenuItem itemImportarXML = new JMenuItem("Importar XML");

        itemExportarXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser selectorArchivo = new JFileChooser();
                int opcionSeleccionada = selectorArchivo.showSaveDialog(null);
                if (opcionSeleccionada == JFileChooser.APPROVE_OPTION) {
                    File fichero = selectorArchivo.getSelectedFile();
                    exportarXML(fichero);
                }

            }
        });

        itemImportarXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser selectorArchivo = new JFileChooser();
                int opcion = selectorArchivo.showOpenDialog(null);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    File fichero = selectorArchivo.getSelectedFile();
                    importarXML(fichero);
                    refrescarComboBox();
                }
            }
        });

        menu.add(itemExportarXML);
        menu.add(itemImportarXML);

        barra.add(menu);
        frame.setJMenuBar(barra);
    }
    private void importarXML(File fichero) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(fichero);

            NodeList moviles = documento.getElementsByTagName("coche");
            for (int i = 0; i < moviles.getLength(); i++) {
                Node movil = moviles.item(i);
                Element elemento = (Element) movil;

                String marca = elemento.getElementsByTagName("marca").item(0).getChildNodes().item(0).getNodeValue();
                String modelo = elemento.getElementsByTagName("modelo").item(0).getChildNodes().item(0).getNodeValue();

                altaMovil(marca, modelo);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private void exportarXML(File fichero) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
            DOMImplementation dom = builder.getDOMImplementation();

            Document documento = dom.createDocument(null, "xml", null);

            Element raiz = documento.createElement("moviles");
            documento.getDocumentElement().appendChild(raiz);

            Element nodoMovil;
            Element nodoDatos;
            Text dato;

            for (Movil movil : lista) {

                nodoMovil = documento.createElement("coche");
                raiz.appendChild(nodoMovil);

                nodoDatos = documento.createElement("marca");
                nodoMovil.appendChild(nodoDatos);

                dato = documento.createTextNode(movil.getMarca());
                nodoDatos.appendChild(dato);

                nodoDatos = documento.createElement("modelo");
                nodoMovil.appendChild(nodoDatos);

                dato = documento.createTextNode(movil.getModelo());
                nodoDatos.appendChild(dato);
            }

            Source src = new DOMSource(documento);
            Result result = new StreamResult(fichero);

            Transformer transformer = null;
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(src, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }



}
