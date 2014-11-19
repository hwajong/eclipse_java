package swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

public class TextEditor extends JFrame implements ActionListener
{
	private String DEFAULT_FONT = "Serif";
	private int DEFAULT_FONT_SIZE = 16;

	private Highlighter.HighlightPainter highlightPainter;

	private JTextArea textArea;

	private JButton btOpen;
	private JComboBox<Integer> comboFontSiz;
	private JTextField tfFind;
	private JButton btFind;

	private JButton btSave;
	private JComboBox<String> comboStyle;
	private JTextField tfRepl;
	private JButton btRepl;

	public TextEditor()
	{
		_createUI();
		_setActionListener();
		_arrangeUI();

		pack();
		setVisible(true);
	}

	// UI 객체 생성
	private void _createUI()
	{
		textArea = new JTextArea(null, 40, 80);
		textArea.setLineWrap(true);
		textArea.setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));

		btOpen = new JButton("Open");
		comboFontSiz = new JComboBox<>();
		for(int i = 8; i <= 40; i += 2)
			comboFontSiz.addItem(i);
		comboFontSiz.setSelectedItem(DEFAULT_FONT_SIZE);

		tfFind = new JTextField();
		btFind = new JButton("Find");

		btSave = new JButton("Save");
		comboStyle = new JComboBox<>();
		comboStyle.addItem("Plain");
		comboStyle.addItem("Bold");
		comboStyle.addItem("Italics");
		tfRepl = new JTextField();
		btRepl = new JButton("Replace");

		highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	}

	// 엑션리스너 등록
	private void _setActionListener()
	{
		btOpen.addActionListener(this);
		comboFontSiz.addActionListener(this);
		tfFind.addActionListener(this);
		btFind.addActionListener(this);
		btSave.addActionListener(this);
		comboStyle.addActionListener(this);
		// tfRepl.addActionListener(this);
		btRepl.addActionListener(this);

		// Quit 확인 처리
		final JFrame mainFrame = this;
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int ret = JOptionPane.showConfirmDialog(mainFrame, "Quit ?", "Confirm", JOptionPane.YES_NO_OPTION);
				if(ret == JOptionPane.YES_OPTION)
				{
					mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
	}

	// UI 배치
	private void _arrangeUI()
	{
		setDefaultLookAndFeelDecorated(true);
		setTitle("Text Editor");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(2, 4));

		bottomPanel.add(btOpen);
		bottomPanel.add(comboFontSiz);
		bottomPanel.add(tfFind);
		bottomPanel.add(btFind);

		bottomPanel.add(btSave);
		bottomPanel.add(comboStyle);
		bottomPanel.add(tfRepl);
		bottomPanel.add(btRepl);

		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void _removeHighlights()
	{
		Highlighter hilite = textArea.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for(int i = 0; i < hilites.length; i++)
			hilite.removeHighlight(hilites[i]);
	}

	private void _applyHighlights()
	{
		String findText = tfFind.getText();
		if(findText.isEmpty()) return;

		Document doc = textArea.getDocument();

		try
		{
			String text = doc.getText(0, doc.getLength());
			int pos = textArea.getCaretPosition();

			// 커서에서 부터 해당 문자열이 없으면 처음부터 다시 찾도록
			if(text.indexOf(findText, pos) < 0) pos = 0;

			// Search for pattern
			Highlighter hilite = textArea.getHighlighter();
			pos = text.indexOf(findText, pos);
			if(pos >= 0)
			{
				hilite.addHighlight(pos, pos + findText.length(), highlightPainter);
				pos += findText.length();
				textArea.setCaretPosition(pos);
			}
		}
		catch(BadLocationException e1)
		{
			// TODO 에러 메세지 박스
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// 파일 오픈 처리
		if(e.getSource() == btOpen)
		{
			JFileChooser fileChooser = new JFileChooser();
			int result = fileChooser.showDialog(this, "Open");
			if(result == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();

				BufferedReader br = null;
				try
				{
					// TODO 텍스트 파일이 아닐경우 처리
					br = new BufferedReader(new FileReader(file));
					textArea.read(br, null);
				}
				catch(IOException ioe)
				{
					// TODO 에러 메세지박스
				}
				finally
				{
					if(br != null)
					{
						try
						{
							br.close();
						}
						catch(IOException e1)
						{
						}
					}
				}
			}
		}
		// 파일 저장 처리
		else if(e.getSource() == btSave)
		{
			JFileChooser fileChooser = new JFileChooser();
			int result = fileChooser.showDialog(this, "Save");
			if(result == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();

				BufferedWriter bw = null;
				try
				{
					// TODO 텍스트 파일이 아닐경우 처리
					bw = new BufferedWriter(new FileWriter(file));
					textArea.write(bw);
				}
				catch(IOException ioe)
				{
					// TODO 에러 메세지박스
				}
				finally
				{
					if(bw != null)
					{
						try
						{
							bw.close();
						}
						catch(IOException e1)
						{
						}
					}
				}
			}
		}
		// 글꼴 스타일 처리
		else if(e.getSource() == comboFontSiz || e.getSource() == comboStyle)
		{
			int fontSize = (int) comboFontSiz.getSelectedItem();
			String strStyle = (String) comboStyle.getSelectedItem();

			int style = Font.PLAIN;
			if("Bold".equals(strStyle)) style = Font.BOLD;
			else if("Italics".equals(strStyle)) style = Font.ITALIC;

			textArea.setFont(new Font(DEFAULT_FONT, style, fontSize));
		}
		// 하일라이트 처리(찾기)
		else if(e.getSource() == tfFind || e.getSource() == btFind)
		{
			_removeHighlights();
			_applyHighlights();
		}
		// replace 처리
		else if(e.getSource() == btRepl)
		{
			Document doc = textArea.getDocument();

			try
			{
				String text = doc.getText(0, doc.getLength());
				text = text.replaceAll(tfFind.getText(), tfRepl.getText());
				textArea.setText(text);
			}
			catch(BadLocationException e1)
			{
				// TODO 에러 메세지박스
			}
		}
	}

	public static void main(String args[])
	{
		new TextEditor();
	}
}
