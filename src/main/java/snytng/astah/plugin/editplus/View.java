package snytng.astah.plugin.editplus;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ClassDiagramEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionEvent;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IEntitySelectionEvent;
import com.change_vision.jude.api.inf.view.IEntitySelectionListener;
import com.change_vision.jude.api.inf.view.IViewManager;

public class View extends JPanel
implements
IPluginExtraTabView,
IEntitySelectionListener,
IDiagramEditorSelectionListener,
ProjectEventListener
{

	/**
	 * logger
	 */
	static final Logger logger = Logger.getLogger(View.class.getName());
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.CONFIG);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	private static final String NAME_DIRECTION_REVERSE = "name_direction_reverse";
	private static final long serialVersionUID = 1L;
	private transient ProjectAccessor projectAccessor = null;
	private transient IDiagramViewManager diagramViewManager = null;

	/**
	 * プロパティファイルの配置場所
	 */
	private static final String VIEW_PROPERTIES = "snytng.astah.plugin.editplus.view";

	/**
	 * リソースバンドル
	 */
	private static final ResourceBundle VIEW_BUNDLE = ResourceBundle.getBundle(VIEW_PROPERTIES, Locale.getDefault());

	public View() {
		try {
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();
		} catch (ClassNotFoundException | InvalidUsingException e) {
			// no action
		}
		initComponents();
	}


	private void initComponents() {
		// レイアウトの設定
		setLayout(new BorderLayout());
		add(createEditPlusPane(), BorderLayout.CENTER);
	}

	private void addListeners() {
		diagramViewManager.addEntitySelectionListener(this);
	}

	private void removeListeners() {
		diagramViewManager.removeEntitySelectionListener(this);
	}


	/*
	 * 関連編集パネル作成
	 */
	JLabel  erLabel = null;
	JButton bNN = new JButton("--x--(U)");
	JButton bAC = new JButton("◇◆-◆◇(J)");
	JButton bAN = new JButton("◇---(J)");
	JButton bCN = new JButton("◆---(K)");
	JButton bNA = new JButton("---◇(L)");
	JButton bNC = new JButton("---◆(;)");

	JButton bGE = new JButton("◁---▷(G)");
	JButton bDE = new JButton("<- ->(Y)");

	JButton bAS = new JButton("◀   ▶(R)");

	JButton bMX = new JButton("--x--(B)");
	JButton bML = new JButton("m----(N)");
	JButton bMR = new JButton("----m(M)");

	JButton bNX = new JButton("--x--(,)");
	JButton bNL = new JButton("<----(.)");
	JButton bNR = new JButton("---->(/)");

	JButton bCT = new JButton("><(C)");

	private void setButtonActionListeners() {
		bNN.addActionListener(getAggregationCompositeActionListener(false, false, false, false));
		bAC.addActionListener(getAggregationCompositeActionListener());
		bAN.addActionListener(getAggregationCompositeActionListener(true , false, false, false));
		bCN.addActionListener(getAggregationCompositeActionListener(false, true , false, false));
		bNA.addActionListener(getAggregationCompositeActionListener(false, false, true , false));
		bNC.addActionListener(getAggregationCompositeActionListener(false, false, false, true ));
		bGE.addActionListener(getGeneralizationActionListener());
		bDE.addActionListener(getDependencyActionListener());
		bAS.addActionListener(getAssociationActionListener());
		bMX.addActionListener(getMultiplicityActionListener(true, true));
		bML.addActionListener(getMultiplicityActionListener(false, true));
		bMR.addActionListener(getMultiplicityActionListener(false, false));
		bNX.addActionListener(getNavigabilityActionListener(/*off*/true, /*left*/true));
		bNL.addActionListener(getNavigabilityActionListener(/*off*/false, /*left*/true));
		bNR.addActionListener(getNavigabilityActionListener(/*off*/false, /*left*/false));
		bCT.addActionListener(getCenterActionListener());
	}

	private void setButtonToolTipTexts(){
		bNN.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.deleteAggregationComposite"));
		bAC.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.modifyAssociation"));
		bAN.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.leftAggregation"));
		bCN.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.leftComposite"));
		bNA.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.rightAggregation"));
		bNC.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.rightComposite"));
		bGE.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.reverseGeneralization"));
		bDE.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.reverseDependency"));
		bAS.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.reverseAssociation"));

		bMX.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.deleteMultiplicity"));
		bML.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.leftMultiplicity"));
		bMR.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.rightMultiplicity"));

		bNX.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.deleteNavigability"));
		bNL.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.leftNavigability"));
		bNR.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.rightNavigability"));

		bCT.setToolTipText(VIEW_BUNDLE.getString("buttonToolTipText.centerize"));
	}

	private void setMnemonicButtons(){
		bNN.setMnemonic(KeyEvent.VK_U);
		bAC.setMnemonic(KeyEvent.VK_J);
		//bAN.setMnemonic(KeyEvent.VK_J);
		//bCN.setMnemonic(KeyEvent.VK_K);
		//bNA.setMnemonic(KeyEvent.VK_L);
		//bNC.setMnemonic(KeyEvent.VK_SEMICOLON);
		bGE.setMnemonic(KeyEvent.VK_G);
		bDE.setMnemonic(KeyEvent.VK_Y);
		bAS.setMnemonic(KeyEvent.VK_R);

		bMX.setMnemonic(KeyEvent.VK_B);
		bML.setMnemonic(KeyEvent.VK_N);
		bMR.setMnemonic(KeyEvent.VK_M);

		bNX.setMnemonic(KeyEvent.VK_COMMA);
		bNL.setMnemonic(KeyEvent.VK_PERIOD);
		bNR.setMnemonic(KeyEvent.VK_SLASH);

		bCT.setMnemonic(KeyEvent.VK_C);
	}

	private void setEnabledButtons(boolean b){
		bNN.setEnabled(b);
		bAC.setEnabled(b);
		bAN.setEnabled(b);
		bCN.setEnabled(b);
		bNA.setEnabled(b);
		bNC.setEnabled(b);
		bGE.setEnabled(b);
		bDE.setEnabled(b);
		bAS.setEnabled(b);

		bMX.setEnabled(b);
		bML.setEnabled(b);
		bMR.setEnabled(b);

		bNX.setEnabled(b);
		bNL.setEnabled(b);
		bNR.setEnabled(b);

		bCT.setEnabled(b);
	}

	// セパレーター
	@SuppressWarnings("serial")
	private JSeparator getSeparator(){
		return new JSeparator(SwingConstants.VERTICAL){
			@Override public Dimension getPreferredSize() {
				return new Dimension(1, 16);
			}
			@Override public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}
		};
	}

	private JPanel getEditRelationsPanel() {
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));

		bPanel.add(new JLabel(VIEW_BUNDLE.getString("paneTitleText.editRelations")));

		bPanel.add(bAS);

		bPanel.add(getSeparator());
		bPanel.add(bNN);
		bPanel.add(bAC);
		//bPanel.add(bAN);
		//bPanel.add(bCN);
		//bPanel.add(bNA);
		//bPanel.add(bNC);

		bPanel.add(getSeparator());
		bPanel.add(bMX);
		bPanel.add(bML);
		bPanel.add(bMR);

		bPanel.add(getSeparator());
		bPanel.add(bNX);
		bPanel.add(bNL);
		bPanel.add(bNR);

		bPanel.add(getSeparator());
		bPanel.add(bGE);
		bPanel.add(bDE);

		return bPanel;
	}


	private int modiyAssociationIndex = 0;
	private boolean[][] associations = new boolean[][]{
		{false, false, false, false},
		{true,  false, false, false},
		{false, false, true,  false},
		{false, true,  false, false},
		{false, false, false, true }
	};
	private ActionListener getAggregationCompositeActionListener() {
		return event -> {
			modiyAssociationIndex++;
			boolean[] bs = associations[modiyAssociationIndex % associations.length];
			modifyAssociation(bs[0], bs[1], bs[2], bs[3]);
		};
	}

	private ActionListener getAggregationCompositeActionListener(final boolean bla, final boolean blc, final boolean bra, final boolean brc){
		return event -> {
			modifyAssociation(bla, blc, bra, brc);
		};
	}

	private void modifyAssociation(final boolean bla, final boolean blc, final boolean bra, final boolean brc) {
		IElement e = selectedPresentation.getModel();

		// 選択された関連の種類を変更
		if (e instanceof IAssociation){
			IAssociation a = (IAssociation)e;
			IAttribute[] att = a.getMemberEnds();

			try {
				TransactionManager.beginTransaction();
				if(att[0].isAggregate() != bla){
					att[0].setAggregation();
				}
				if(att[0].isComposite() != blc){
					att[0].setComposite();
				}
				if(att[1].isAggregate() != bra){
					att[1].setAggregation();
				}
				if(att[1].isComposite() != brc){
					att[1].setComposite();
				}
				TransactionManager.endTransaction();
			} catch (InvalidEditingException iee) {
				TransactionManager.abortTransaction();
			}
		}

		// 関連名の読み上げを更新
		String rel = RelationReader.printRelation(e);
		erLabel.setText(rel);
	}

	private ActionListener getGeneralizationActionListener(){
		return event -> {
			IElement e = selectedPresentation.getModel();

			// 選択された関連の種類を変更
			if (e instanceof IGeneralization){
				IGeneralization g = (IGeneralization)e;
				IClass superType = g.getSuperType();
				IClass subType = g.getSubType();

				try {
					IViewManager vm = projectAccessor.getViewManager();
					IDiagramViewManager dvm = vm.getDiagramViewManager();
					IDiagram diagram = dvm.getCurrentDiagram();
					if(! (diagram instanceof IClassDiagram)){
						return;
					}

					IClassDiagram classDiagram = (IClassDiagram)diagram;

					TransactionManager.beginTransaction();

					BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
					bme.delete(g);
					g = bme.createGeneralization(superType, subType, "");

					// ClassDiagramEditorを取得する。
					ClassDiagramEditor cde = projectAccessor.getDiagramEditorFactory().getClassDiagramEditor();
					cde.setDiagram(classDiagram);

					// superTypeとsubTypeのIPresentationを取得する
					IPresentation[] ps = classDiagram.getPresentations();
					INodePresentation superP = null;
					INodePresentation subP = null;
					for(IPresentation p : ps){
						if(p.getModel() == g.getSuperType()){
							superP = (INodePresentation)p;
						} else if(p.getModel() == g.getSubType()){
							subP = (INodePresentation)p;
						}
					}
					if(superP != null && subP != null){
						selectedPresentation = cde.createLinkPresentation(g, superP , subP);
					}

					TransactionManager.endTransaction();
				} catch (Exception ex) {
					TransactionManager.abortTransaction();
				}
			}

			// 関連名の読み上げを更新
			String rel = RelationReader.printRelation(e);
			erLabel.setText(rel);
		};
	}

	private ActionListener getDependencyActionListener(){
		return event -> {
			IElement e = selectedPresentation.getModel();

			// 選択された関連の種類を変更
			if (e instanceof IDependency){
				IDependency d = (IDependency)e;
				INamedElement client = d.getClient();
				INamedElement supplier = d.getSupplier();

				try {
					IViewManager vm = projectAccessor.getViewManager();
					IDiagramViewManager dvm = vm.getDiagramViewManager();
					IDiagram diagram = dvm.getCurrentDiagram();
					if(! (diagram instanceof IClassDiagram)){
						return;
					}

					IClassDiagram classDiagram = (IClassDiagram)diagram;

					TransactionManager.beginTransaction();

					BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
					bme.delete(d);
					d = bme.createDependency(client, supplier, "");

					// ClassDiagramEditorを取得する。
					ClassDiagramEditor cde = projectAccessor.getDiagramEditorFactory().getClassDiagramEditor();
					cde.setDiagram(classDiagram);

					// superTypeとsubTypeのIPresentationを取得する
					IPresentation[] ps = classDiagram.getPresentations();
					INodePresentation clientP = null;
					INodePresentation supplierP = null;
					for(IPresentation p : ps){
						if(p.getModel() == d.getClient()){
							clientP = (INodePresentation)p;
						} else if(p.getModel() == d.getSupplier()){
							supplierP = (INodePresentation)p;
						}
					}
					if(clientP != null && supplierP != null){
						selectedPresentation = cde.createLinkPresentation(d, clientP , supplierP);
					}

					TransactionManager.endTransaction();
				} catch (Exception ex) {
					TransactionManager.abortTransaction();
					ex.printStackTrace();
				}
			}

			// 関連名の読み上げを更新
			String rel = RelationReader.printRelation(e);
			erLabel.setText(rel);
		};
	}

	private ActionListener getAssociationActionListener(){
		return event -> {
			IElement e = selectedPresentation.getModel();

			// 選択された関連の種類を変更
			if (e instanceof IAssociation){
				IAssociation a = (IAssociation)e;

				try {
					TransactionManager.beginTransaction();
					// 関連名の読み方の方向＝▲の方向
					// IPresentationのname_direction_reverseが0なら関連の方向と同じ、1ながら関連の方向と反対
					// 関連の方向を入れ替える
					IPresentation[] ps = a.getPresentations();
					if(ps.length > 0){
						IPresentation p = ps[0];
						String direction = p.getProperty(NAME_DIRECTION_REVERSE);
						if(direction.equals("0")){
							p.setProperty(NAME_DIRECTION_REVERSE, "1");
						} else {
							p.setProperty(NAME_DIRECTION_REVERSE, "0");
						}
					}
					TransactionManager.endTransaction();
				} catch (Exception ex) {
					TransactionManager.abortTransaction();
				}
			}

			// 関連名の読み上げを更新
			String rel = RelationReader.printRelation(e);
			erLabel.setText(rel);
		};
	}

	private int attrmultiplicity0index = 0;
	private int attrmultiplicity1index = 0;
	private String[][][] multiplicities = new String[][][]{
		{{""}},
		{{"1"}},
		{{"0","1"}},
		{{"0","*"}},
		{{"*"}},
		{{"1","*"}}
	};

	private ActionListener getMultiplicityActionListener(final boolean off, final boolean left){
		return event -> {
			IElement e = selectedPresentation.getModel();

			// 選択された関連の種類を変更
			if (e instanceof IAssociation){
				IAssociation a = (IAssociation)e;

				try {
					TransactionManager.beginTransaction();
					IAttribute[] attr = a.getMemberEnds();
					if(off){
						attr[0].setMultiplicityStrings(new String[][]{{""}});
						attr[1].setMultiplicityStrings(new String[][]{{""}});
						attrmultiplicity0index = 0;
						attrmultiplicity1index = 0;
					} else if(left){
						attrmultiplicity0index++;
						attr[0].setMultiplicityStrings(multiplicities[attrmultiplicity0index % multiplicities.length]);
					} else {
						attrmultiplicity1index++;
						attr[1].setMultiplicityStrings(multiplicities[attrmultiplicity1index % multiplicities.length]);
					}
					TransactionManager.endTransaction();
				} catch (Exception ex) {
					TransactionManager.abortTransaction();
				}
			}

			// 関連名の読み上げを更新
			String rel = RelationReader.printRelation(e);
			erLabel.setText(rel);
		};
	}

	private int attrnavigbility0index = 0;
	private int attrnavigbility1index = 0;
	private String[] navigabilities = new String[]{
			"Unspecified","Navigable","Non_Navigable"
	};
	private ActionListener getNavigabilityActionListener(final boolean off, final boolean left){
		return event -> {
			IElement e = selectedPresentation.getModel();

			// 選択された関連の種類を変更
			if (e instanceof IAssociation){
				IAssociation a = (IAssociation)e;

				try {
					TransactionManager.beginTransaction();
					IAttribute[] attr = a.getMemberEnds();
					if(off){
						attr[0].setNavigability(navigabilities[0]);
						attr[1].setNavigability(navigabilities[0]);
						attrnavigbility0index = 0;
						attrnavigbility1index = 0;
					} else if(left){
						attrnavigbility0index++;
						attr[0].setNavigability(navigabilities[attrnavigbility0index % navigabilities.length]);
					} else {
						attrnavigbility1index++;
						attr[1].setNavigability(navigabilities[attrnavigbility1index % navigabilities.length]);
					}
					TransactionManager.endTransaction();
				} catch (Exception ex) {
					TransactionManager.abortTransaction();
				}
			}

			// 関連名の読み上げを更新
			String rel = RelationReader.printRelation(e);
			erLabel.setText(rel);
		};
	}

	private ActionListener getCenterActionListener(){
		return event -> {
			// 移動
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				if(ps.length > 0){
					TransactionManager.beginTransaction();
					diagramViewManager.showInDiagramEditor(ps[0]);
					TransactionManager.endTransaction();
				}

			} catch (InvalidUsingException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
		};
	}

	private enum FlipDirection {HORIZONTAL, VERTICAL}

	@SuppressWarnings("unchecked")
	private ActionListener getFlipActionListener (FlipDirection flipDirection){
		return event -> {
			// 選択要素の取得
			// 今選択している図のタイプを取得する
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				Arrays.stream(ps)
				.forEach(p -> {
					logger.log(Level.INFO, "Presentation=" + p.getType() + "(" + p.getClass() + ")");
					logger.log(Level.INFO, () ->
					(String)p.getProperties().keySet().stream()
					.sorted()
					.map(k -> (String)k + "=" + p.getProperty((String)k))
					.collect(Collectors.joining(System.lineSeparator())));
				});

				INodePresentation[] nps = Arrays.stream(ps)
						.filter(p -> p instanceof INodePresentation)
						.filter(p -> p.getLabel() != null)
						.toArray(INodePresentation[]::new);

				ILinkPresentation[] lps = Arrays.stream(ps)
						.filter(p -> p instanceof ILinkPresentation)
						.toArray(ILinkPresentation[]::new);


				double xmin = Double.MAX_VALUE;
				double ymin = Double.MAX_VALUE;
				double xmax = Double.MIN_VALUE;
				double ymax = Double.MIN_VALUE;

				for(INodePresentation np : nps) {
					Point2D point = np.getLocation();
					double x = point.getX();
					double y = point.getY();
					double w = np.getWidth();
					double h = np.getHeight();

					if(x < xmin)     xmin = x;
					if(y < ymin)     ymin = y;
					if(x + w > xmax) xmax = x + w;
					if(y + h > ymax) ymax = y + h;
				}

				for(ILinkPresentation lp : lps){
					for(Point2D point : lp.getAllPoints()){
						double x = point.getX();
						double y = point.getY();

						if(x < xmin) xmin = x;
						if(y < ymin) ymin = y;
						if(x > xmax) xmax = x;
						if(y > ymax) ymax = y;
					}
				}

				double x0 = (xmin + xmax)/2d;
				double y0 = (ymin + ymax)/2d;

				try {
					TransactionManager.beginTransaction();

					for(INodePresentation np : nps) {
						Point2D point = np.getLocation();
						double x = point.getX();
						double y = point.getY();
						double w = np.getWidth();
						double h = np.getHeight();

						if(flipDirection == FlipDirection.HORIZONTAL){
							double nx = 2d*x0 - x - w;
							point.setLocation(nx, y);
							np.setLocation(point);
						} else if(flipDirection == FlipDirection.VERTICAL){
							double ny = 2d*y0 - y - h;
							point.setLocation(x, ny);
							np.setLocation(point);
						}
					}

					for(ILinkPresentation lp : lps) {
						Point2D[] lpps = lp.getPoints();
						Point2D[] points = new Point2D[lpps.length];
						for(int i = 0; i < lpps.length; i++){
							Point2D point = lpps[i];
							double x = point.getX();
							double y = point.getY();

							if(flipDirection == FlipDirection.HORIZONTAL){
								double nx = 2d*x0 - x;
								point.setLocation(nx, y);
							} else if(flipDirection == FlipDirection.VERTICAL){
								double ny = 2d*y0 - y;
								point.setLocation(x, ny);
							}
							points[i] = point;
						}
						lp.setAllPoints(points);
					}

					TransactionManager.endTransaction();
				} catch (InvalidEditingException e) {
					TransactionManager.abortTransaction();
				}
			} catch (InvalidUsingException e) {
			}

		};
	}

	private ActionListener getRotationActionListener (){
		return event -> {
			// 選択要素の取得
			// 今選択している図のタイプを取得する
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();


				INodePresentation[] nps = Arrays.stream(ps)
						.filter(p -> p instanceof INodePresentation)
						.filter(p -> p.getLabel() != null)
						.toArray(INodePresentation[]::new);

				ILinkPresentation[] lps = Arrays.stream(ps)
						.filter(p -> p instanceof ILinkPresentation)
						.toArray(ILinkPresentation[]::new);


				double xmin = Double.MAX_VALUE;
				double ymin = Double.MAX_VALUE;
				double xmax = Double.MIN_VALUE;
				double ymax = Double.MIN_VALUE;

				for(INodePresentation np : nps) {
					Point2D point = np.getLocation();
					double x = point.getX();
					double y = point.getY();
					double w = np.getWidth();
					double h = np.getHeight();

					if(x < xmin)     xmin = x;
					if(y < ymin)     ymin = y;
					if(x + w > xmax) xmax = x + w;
					if(y + h > ymax) ymax = y + h;
				}

				for(ILinkPresentation lp : lps){
					for(Point2D point : lp.getAllPoints()){
						double x = point.getX();
						double y = point.getY();

						if(x < xmin) xmin = x;
						if(y < ymin) ymin = y;
						if(x > xmax) xmax = x;
						if(y > ymax) ymax = y;
					}
				}

				double x0 = (xmin + xmax)/2d;
				double y0 = (ymin + ymax)/2d;

				try {
					TransactionManager.beginTransaction();

					for(INodePresentation np : nps) {
						Point2D point = np.getLocation();
						double x = point.getX();
						double y = point.getY();
						double w = np.getWidth();
						double h = np.getHeight();

						double nx = -(y + h/2 - y0) + x0 - w/2;
						double ny =  (x + w/2 - x0) + y0 - h/2;
						point.setLocation(nx, ny);
						np.setLocation(point);
					}

					for(ILinkPresentation lp : lps) {
						Point2D[] lpps = lp.getPoints();
						Point2D[] points = new Point2D[lpps.length];
						for(int i = 0; i < lpps.length; i++){
							Point2D point = lpps[i];
							double x = point.getX();
							double y = point.getY();

							double nx = -(y - y0) + x0;
							double ny =  (x - x0) + y0;
							point.setLocation(nx, ny);
							points[i] = point;
						}

						lp.setAllPoints(points);
					}

					TransactionManager.endTransaction();
				} catch (InvalidEditingException e) {
					TransactionManager.abortTransaction();
				}
			} catch (InvalidUsingException e) {
			}

		};
	}

	private ActionListener getSelectClassListener (){
		return event -> {
			// 選択要素の取得
			// 今選択している図のタイプを取得する
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				// IClassのINodePresentationを抽出
				INodePresentation[] nps = Arrays.stream(ps)
						.filter(p -> p instanceof INodePresentation)
						.filter(p -> p.getModel() instanceof IClass)
						.toArray(INodePresentation[]::new);

				dvm.select(nps);

			} catch (InvalidUsingException e) {
				logger.log(Level.WARNING, e.getMessage());
			}

		};
	}


	private ActionListener getAssociationNameActionListener(){
		return event -> {

			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				Arrays.stream(ps)
				.filter(p -> p.getModel() instanceof IAssociation)
				.map(ILinkPresentation.class::cast)
				.forEach( lp -> {
					try {
						TransactionManager.beginTransaction();

						double len = 0d;
						Point2D startP2d = null;
						for(Point2D p2d: lp.getAllPoints()){
							if(startP2d == null){
								startP2d = p2d;
							} else {
								len += Math.sqrt(Math.pow(startP2d.getX() - p2d.getX(),2d) + Math.pow(startP2d.getY() - p2d.getY(), 2d));
								startP2d = p2d;
							}
						}

						double lmid = len/2d;

						len = 0d;
						startP2d = null;
						double newX = 0d;
						double newY = 0d;
						for(Point2D p2d: lp.getAllPoints()){
							if(startP2d == null){
								startP2d = p2d;
							} else {
								double lnow = Math.sqrt(Math.pow(startP2d.getX() - p2d.getX(), 2d) + Math.pow(startP2d.getY() - p2d.getY(), 2d));
								if(len + lnow > lmid){
									newX = startP2d.getX() + (lmid-len)/lnow*(p2d.getX() - startP2d.getX());
									newY = startP2d.getY() + (lmid-len)/lnow*(p2d.getY() - startP2d.getY());
									break;
								}
								len += lnow;
								startP2d = p2d;
							}
						}

						lp.setProperty("name.point.x", Double.toString(newX));
						lp.setProperty("name.point.y", Double.toString(newY));

						TransactionManager.endTransaction();
					} catch (Exception ex) {
						TransactionManager.abortTransaction();
					}
				});

			} catch (InvalidUsingException e1) {
			}

		};
	}

	private static final String FILL_COLOR = "fill.color";
	private static final String LINE_COLOR = "line.color";

	private ActionListener getSyncColorActionListener(){
		return event -> {
			// 選択要素の取得
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				IPresentation[] cs = Arrays.stream(ps)
						.filter(p -> p.getModel() instanceof IComment)
						.toArray(IPresentation[]::new);

				if(cs.length == 0){
					return;
				}

				String bgColorString = cs[0].getProperty(FILL_COLOR);

				TransactionManager.beginTransaction();

				Arrays.stream(ps)
				.filter(p -> ! (p.getModel() instanceof IComment))
				.filter(p -> p.getProperty(FILL_COLOR) != null)
				.forEach(p ->
				{
					try{
						p.setProperty(FILL_COLOR, bgColorString);
					}catch (InvalidEditingException e) {
					}
				});

				TransactionManager.endTransaction();
			} catch (InvalidUsingException e) {
			}
		};
	}

	private transient IPresentation[] selectedPresentations = null;
	private ActionListener getColorPickerActionListener(){
		return event -> {
			// 選択要素の取得
			// 今選択している図のタイプを取得する
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				selectedPresentations = dvm.getSelectedPresentations();

				// デバッグ出力
				logger.log(Level.FINE, "selectedPresentations:");
				logger.log(Level.FINE, () ->
				Arrays.stream(selectedPresentations)
				.map(IPresentation::getLabel)
				.sorted()
				.collect(Collectors.joining(System.lineSeparator())));

			} catch (InvalidUsingException e) {
			}

		};
	}

	private ActionListener getSyncStereotypeActionListener(boolean add){
		return event -> {
			// 選択要素の取得
			// 今選択している図のタイプを取得する
			IViewManager vm;
			try {
				vm = projectAccessor.getViewManager();
				IDiagramViewManager dvm = vm.getDiagramViewManager();
				IPresentation[] ps = dvm.getSelectedPresentations();

				IPresentation[] cs = Arrays.stream(ps)
						.filter(p -> p.getModel() instanceof IComment)
						.toArray(IPresentation[]::new);

				if(cs.length == 0){
					return;
				}

				String stereotype = cs[0].getLabel();

				TransactionManager.beginTransaction();

				Arrays.stream(ps)
				.filter(p -> ! (p.getModel() instanceof IComment))
				.filter(p -> p.getLabel() != null)
				.map(IPresentation::getModel)
				.forEach(e ->
				{
					boolean hasStereotype = Arrays.asList(e.getStereotypes()).contains(stereotype);
					try{
						if(add && ! hasStereotype) {
							e.addStereotype(stereotype);
						} else if(! add && hasStereotype) {
							e.removeStereotype(stereotype);
						} else {
							// do nothing
						}

					}catch (InvalidEditingException ex) {
					}
				});

				TransactionManager.endTransaction();
			} catch (InvalidUsingException e) {
			}
		};
	}

	private Container createEditPlusPane() {
		JPanel lPanel = new JPanel();
		lPanel.setLayout(new BorderLayout());

		erLabel = new JLabel(VIEW_BUNDLE.getString("puglicExtraTabView.pleaseSelctRelation"));
		lPanel.add(erLabel, BorderLayout.CENTER);

		setEnabledButtons(false);
		setMnemonicButtons();
		setButtonActionListeners();
		setButtonToolTipTexts();

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setAlignmentX(LEFT_ALIGNMENT);
		centerPanel.add(getEditRelationsPanel());
		centerPanel.add(getEditElementsPanel());

		JPanel erPanel = new JPanel();
		erPanel.setLayout(new BorderLayout());
		erPanel.add(lPanel, BorderLayout.NORTH);
		erPanel.add(centerPanel, BorderLayout.CENTER);
		erPanel.add(bCT, BorderLayout.SOUTH);

		return new JScrollPane(erPanel);
	}

	private JPanel getEditElementsPanel(){
		JPanel fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.X_AXIS));

		// 位置変更
		JButton bFlipVertical = new JButton("上下反転");
		bFlipVertical.addActionListener(getFlipActionListener(FlipDirection.VERTICAL));
		JButton bFlipHorizontal = new JButton("左右反転");
		bFlipHorizontal.addActionListener(getFlipActionListener(FlipDirection.HORIZONTAL));
		JButton bClockwiseRotation90 = new JButton("右90度回転");
		bClockwiseRotation90.addActionListener(getRotationActionListener());
		// 選択変更
		JButton bSelectClass = new JButton("クラス選択");
		bSelectClass.addActionListener(getSelectClassListener());
		//　関連名
		JButton bAssociationName = new JButton("関連名整列");
		bAssociationName.addActionListener(getAssociationNameActionListener());
		// 色ピッカー
		JButton bColorPicker = new JButton("色スポイト");
		bColorPicker.addActionListener(getColorPickerActionListener());
		// ノート活用
		JButton bSyncColor = new JButton("ノート色反映");
		bSyncColor.addActionListener(getSyncColorActionListener());
		JButton bAddStereotype = new JButton("ノートステレオタイプ+");
		bAddStereotype.addActionListener(getSyncStereotypeActionListener(true));
		JButton bRemoveStereotype = new JButton("ノートステレオタイプ-");
		bRemoveStereotype.addActionListener(getSyncStereotypeActionListener(false));

		fPanel.add(new JLabel(VIEW_BUNDLE.getString("paneTitleText.editElements")));
		fPanel.add(bFlipVertical);
		fPanel.add(bFlipHorizontal);
		fPanel.add(bClockwiseRotation90);
		fPanel.add(bSelectClass);
		fPanel.add(bAssociationName);
		fPanel.add(bColorPicker);
		fPanel.add(bSyncColor);
		fPanel.add(bAddStereotype);
		fPanel.add(bRemoveStereotype);

		return fPanel;
	}

	/**
	 * 要素の選択が変更されたら、クラス数の表示を更新する
	 */
	@Override
	public void entitySelectionChanged(IEntitySelectionEvent arg0) {
		updateDiagramView();
	}

	private void updateDiagramView(){
		try {
			// 今選択している図のタイプを取得する
			IViewManager vm = projectAccessor.getViewManager();
			IDiagramViewManager dvm = vm.getDiagramViewManager();
			IDiagram diagram = dvm.getCurrentDiagram();

			// 選択している図がクラス図ならば、関連編集パネルを更新する
			if(diagram instanceof IClassDiagram){
				updateClassDiagram(dvm);
			}

		}catch(Exception ex){
		}
	}

	private transient IPresentation selectedPresentation = null;

	@SuppressWarnings("unchecked")
	private void updateClassDiagram(IDiagramViewManager dvm) {
		erLabel.setText(VIEW_BUNDLE.getString("puglicExtraTabView.pleaseSelctRelation"));
		setEnabledButtons(false);
		selectedPresentation = null;

		// 選択要素の取得
		IPresentation[] presentation = dvm.getSelectedPresentations();

		// デバッグ出力
		logger.log(Level.INFO, "updateClassDiagram");
		logger.log(Level.INFO, () ->
		Arrays.stream(dvm.getSelectedPresentations())
		.map(p -> "Presentation p=" + p + ", model=" + p.getModel())
		.collect(Collectors.joining(System.lineSeparator())));
		logger.log(Level.INFO, () ->
		Arrays.stream(dvm.getSelectedElements())
		.map(e -> "Element e=" + e)
		.collect(Collectors.joining(System.lineSeparator())));

		// 選択要素の1つめを中心に移動できるようにする
		if(presentation.length > 0){
			selectedPresentation = presentation[0];
			bCT.setEnabled(true);
		}

		// 選択要素が一つのとき
		if(presentation.length == 1){
			IPresentation p = presentation[0];
			selectedPresentation = p;

			// デバッグ出力
			logger.log(Level.INFO, "IPresentation:" + p.getLabel());
			logger.log(Level.INFO, () ->
			(String)p.getProperties().keySet().stream()
			.sorted()
			.map(k ->  (String)k + "=" + p.getProperty((String)k))
			.collect(Collectors.joining(System.lineSeparator())));

			// 関連のとき
			if(RelationReader.isSupportedRelation(p.getModel())) {
				String rel = RelationReader.printRelation(p.getModel());
				if(rel != null){
					erLabel.setText(rel);
					setEnabledButtons(true);
				}

				// デバッグ出力
				ILinkPresentation l = (ILinkPresentation)p;
				logger.log(Level.INFO, "ILinkPresentation:" + l.getLabel());
				logger.log(Level.INFO, () ->
				Arrays.stream(l.getAllPoints())
				.map(Point2D::toString)
				.collect(Collectors.joining(System.lineSeparator())));

			}

			// 色合わせモードがONのとき
			if(selectedPresentations != null){

				Arrays.stream(selectedPresentations)
				.filter(pr -> pr.getClass() == p.getClass())  // 同じIPresentaionを選択
				.forEach(pr -> {

					// FILL_COLORを持つIPresentationのとき
					if(p.getProperty(FILL_COLOR) != null){
						String fillColor = p.getProperty(FILL_COLOR);
						try {
							TransactionManager.beginTransaction();
							pr.setProperty(FILL_COLOR, fillColor);
							TransactionManager.endTransaction();
						} catch (InvalidEditingException e1) {
							TransactionManager.abortTransaction();
						}
					}

					// LINE_COLORを持つIPresentationのとき
					if(p.getProperty(LINE_COLOR) != null){
						String lineColor = p.getProperty(LINE_COLOR);

						try {
							TransactionManager.beginTransaction();
							pr.setProperty(LINE_COLOR, lineColor);
							TransactionManager.endTransaction();
						} catch (InvalidEditingException e1) {
							TransactionManager.abortTransaction();
						}
					}

				});
			}
		}
		// 色合わせモードを解除
		selectedPresentations = null;
	}

	@Override
	public void addSelectionListener(ISelectionListener listener) {
		// no action
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return VIEW_BUNDLE.getString("pluginExtraTabView.description");
	}
	@Override
	public String getTitle() {
		return VIEW_BUNDLE.getString("pluginExtraTabView.title");
	}

	public void activated() {
		// リスナーへの登録
		addListeners();
	}

	public void deactivated() {
		// リスナーへの登録解除
		removeListeners();
	}


	@Override
	public void projectChanged(ProjectEvent arg0) {
		updateDiagramView();
	}


	@Override
	public void projectClosed(ProjectEvent arg0) {
		// no action
	}


	@Override
	public void projectOpened(ProjectEvent arg0) {
		// no action
	}


	@Override
	public void diagramSelectionChanged(IDiagramEditorSelectionEvent arg0) {
		updateDiagramView();
	}
}
