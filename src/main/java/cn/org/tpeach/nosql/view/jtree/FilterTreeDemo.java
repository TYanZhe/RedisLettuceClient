package cn.org.tpeach.nosql.view.jtree;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;

public class FilterTreeDemo {
  public static void main(String[] args) throws FileNotFoundException {
    EventQueue.invokeLater(new ShowIt());
  }
}

class FiltNode extends DefaultMutableTreeNode { 
  FiltNode( Object user_obj ){
    super( user_obj );
  }
  FiltNode m_counterpart_node;

//  public String toString(){
//    // hash code demonstrates (as you toggle) that these are not the same nodes...
//    return super.toString() + " (" + hashCode() + ")"; 
//  }
}

class FilterPair {

  TreeCoupling m_on_coupling, m_off_coupling;
  boolean m_filter_on = true;
  JFrame m_main_frame;
  FiltNode m_on_root = new FiltNode( "root" );
  FiltNode m_off_root = new FiltNode( "root" );

  // needed to prevent infinite calling between models...  
  boolean m_is_propagated_call = false;

  FilterPair( JFrame main_frame ){
    m_on_root.m_counterpart_node = m_off_root;
    m_off_root.m_counterpart_node = m_on_root;
    m_on_coupling = new TreeCoupling( true ); 
    m_off_coupling = new TreeCoupling( false );
    m_main_frame = main_frame;
    // starts by toggling to OFF (i.e. before display)
    toggle_filter();
  }

  // this is the filter method for this particular FilterPair...
  boolean is_filtered_out( MutableTreeNode node ){
    return node.toString().contains( "nobble");
  }


  class TreeCoupling {


    class FilterTreeModel extends DefaultTreeModel {
      FilterTreeModel( TreeNode root ){
        super( root );
      }

      public void insertNodeInto(MutableTreeNode new_child, MutableTreeNode parent, int index){
        // aliases for convenience
        FiltNode new_filt_node = (FiltNode)new_child;
        FiltNode parent_filt_node = (FiltNode)parent;

        FiltNode new_counterpart_filt_node = null;
        FiltNode counterpart_parent_filt_node = null;
        // here and below the propagation depth test is used to skip code which is leading to another call to 
        // insertNodeInto on the counterpart TreeModel...
        if( ! m_is_propagated_call ){
          // NB the counterpart new FiltNode is given exactly the same user object as its counterpart: no duplication
          // of the user object...
          new_counterpart_filt_node = new FiltNode( new_filt_node.getUserObject() );
          counterpart_parent_filt_node = parent_filt_node.m_counterpart_node;
          // set up the 2 counterpart relationships between the node in the ON tree and the node in the OFF tree
          new_counterpart_filt_node.m_counterpart_node = new_filt_node;
          new_filt_node.m_counterpart_node = new_counterpart_filt_node;
        }

        if( TreeCoupling.this == m_on_coupling ){
          // ... we are in the ON coupling

          // if first call and the parent has no counterpart (i.e. in the OFF coupling) sthg has gone wrong
          if( ! m_is_propagated_call && counterpart_parent_filt_node == null ){
            throw new NullPointerException();
          }
          if( ! is_filtered_out( new_filt_node ) ){
            // only insert here (ON coupling) if the node is NOT filtered out...
            super.insertNodeInto( new_filt_node, parent_filt_node, index);
          }
          else {
            // enable the originally submitted new node (now rejected) to be unlinked and garbage-collected...
            // (NB if you suspect the first line here is superfluous, try commenting out and see what happens)
            new_filt_node.m_counterpart_node.m_counterpart_node = null;
            new_filt_node.m_counterpart_node = null;
          }
          if( ! m_is_propagated_call  ){
            // as we are in the ON coupling we can't assume that the index value should be passed on unchanged to the
            // OFF coupling: some siblings (of lower index) may be missing here... but we **do** know that the previous 
            // sibling (if there is one) of the new node has a counterpart in the OFF tree... so get the index of its
            // OFF counterpart and add 1...
            int off_index = 0;
            if( index > 0 ){
              FiltNode prev_sib = (FiltNode)parent_filt_node.getChildAt( index - 1 );
              off_index = counterpart_parent_filt_node.getIndex( prev_sib.m_counterpart_node ) + 1;
            }
            m_is_propagated_call = true;
            m_off_coupling.m_tree_model.insertNodeInto( new_counterpart_filt_node, counterpart_parent_filt_node, off_index);
          }

        }
        else {
          // ... we are in the OFF coupling

          super.insertNodeInto( new_filt_node, parent_filt_node, index);
          if( ! m_is_propagated_call  ){

            // we are in the OFF coupling: it is perfectly legitimate for the parent to have no counterpart (i.e. in the 
            // ON coupling: indicates that it, or an ancestor of it, has been filtered out)
            if( counterpart_parent_filt_node != null ){
              // OTOH, if the parent **is** available, we can't assume that the index value should be passed on unchanged: 
              // some siblings of the new incoming node (of lower index) may have been filtered out... to find the 
              // correct index value we track down the index value until we reach a node which has a counterpart in the 
              // ON coupling... or if not found the index must be 0 
              int on_index = 0;
              if( index > 0 ){
                for( int i = index - 1; i >= 0; i-- ){
                  FiltNode counterpart_sib = ((FiltNode)parent_filt_node.getChildAt( i )).m_counterpart_node;
                  if( counterpart_sib != null ){
                    on_index = counterpart_parent_filt_node.getIndex( counterpart_sib ) + 1;
                    break;
                  }
                }
              }
              m_is_propagated_call = true;
              m_on_coupling.m_tree_model.insertNodeInto( new_counterpart_filt_node, counterpart_parent_filt_node, on_index);
            }
            else {
              // ... no ON-coupling parent node "counterpart": the new ON node must be discarded  
              new_filt_node.m_counterpart_node = null;
            }


          }
        }
        m_is_propagated_call = false;
      }
    }

    JTree m_tree;
    FilterTreeModel m_tree_model;
    TreeCoupling( boolean on ){
      m_tree = new JTree();
      m_tree_model = on ? new FilterTreeModel( m_on_root ) : new FilterTreeModel( m_off_root ); 
      m_tree.setModel( m_tree_model );
    }
  }

   void toggle_filter(){
    m_filter_on = ! m_filter_on;
    m_main_frame.setTitle( m_filter_on? "FilterTree - ON (Ctrl-F6 to toggle)" : "FilterTree - OFF (Ctrl-F6 to toggle)" ); 
  }

  TreeCoupling getCurrCoupling(){
    return m_filter_on? m_on_coupling : m_off_coupling;
  }
}


class ShowIt implements Runnable {
  @Override
  public void run() {
    JFrame frame = new JFrame("FilterTree");
    final FilterPair pair = new FilterPair( frame ); 
    final JScrollPane jsp = new JScrollPane( pair.getCurrCoupling().m_tree );
    Action toggle_between_views = new AbstractAction( "toggle filter" ){
      @Override
      public void actionPerformed(ActionEvent e) {
        pair.toggle_filter();
        jsp.getViewport().setView( pair.getCurrCoupling().m_tree );
        jsp.requestFocus();
      }};
    JPanel cpane = (JPanel)frame.getContentPane(); 
    cpane.getActionMap().put("toggle between views", toggle_between_views );
    InputMap new_im = new InputMap();
    new_im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK), "toggle between views");
    cpane.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new_im);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(jsp);
    frame.pack();
    frame.setBounds(50, 50, 800, 500);
    frame.setVisible(true);

    // populate the tree(s) NB we are currently viewing the OFF tree
    FilterPair.TreeCoupling curr_coupling = pair.getCurrCoupling(); 
    curr_coupling.m_tree_model.insertNodeInto( new FiltNode( "scrags 1" ), (FiltNode)curr_coupling.m_tree_model.getRoot(), 0 );
    FiltNode d2 = new FiltNode( "scrags 2" );
    curr_coupling.m_tree_model.insertNodeInto( d2, (FiltNode)curr_coupling.m_tree_model.getRoot(), 1 );
    curr_coupling.m_tree_model.insertNodeInto( new FiltNode( "scrags 3" ), (FiltNode)curr_coupling.m_tree_model.getRoot(), 2 );
    curr_coupling.m_tree_model.insertNodeInto( new FiltNode( "scrags 2.1" ), d2, 0 );

    // this will be filtered out of the ON tree
    FiltNode nobble = new FiltNode( "nobble" );
    curr_coupling.m_tree_model.insertNodeInto( nobble, d2, 1 );
    // this will also be filtered out of the ON tree
    FiltNode son_of_nobble = new FiltNode( "son of nobble");
    curr_coupling.m_tree_model.insertNodeInto( son_of_nobble, nobble, 0 );    

    curr_coupling.m_tree_model.insertNodeInto( new FiltNode( "peewit (granddaughter of n****e)"), son_of_nobble, 0 );    

    // expand the OFF tree
    curr_coupling.m_tree.expandPath( new TreePath( curr_coupling.m_tree_model.getRoot() ) );
    curr_coupling.m_tree.expandPath( new TreePath( d2.getPath() ) );
    curr_coupling.m_tree.expandPath( new TreePath( nobble.getPath() ) );
    curr_coupling.m_tree.expandPath( new TreePath( son_of_nobble.getPath() ) );

    // switch view (programmatically) to the ON tree
    toggle_between_views.actionPerformed( null );

    // expand the ON tree
    curr_coupling = pair.getCurrCoupling();
    curr_coupling.m_tree.expandPath( new TreePath( curr_coupling.m_tree_model.getRoot() ) );
    curr_coupling.m_tree.expandPath( new TreePath( d2.m_counterpart_node.getPath() ) );

    // try to expand the counterpart of "nobble"... there shouldn't be one...
    FiltNode nobble_counterpart = nobble.m_counterpart_node;
    if( nobble_counterpart != null ){
      curr_coupling.m_tree.expandPath( new TreePath( nobble_counterpart.getPath() ) );
      System.err.println( "oops..." );
    }
    else {
      System.out.println( "As expected, node \"nobble\" has no counterpart in the ON coupling" );
    }



    // try inserting a node into the ON tree which will immediately be "rejected" by the ON tree (due to being 
    // filtered out before the superclass insertNodeInto is called), but will nonetheless appear in the 
    // OFF tree as it should...
    curr_coupling.m_tree_model.insertNodeInto( new FiltNode( "yet another nobble"), d2.m_counterpart_node, 0 );


  }
}