package computergraphics.scenegraph;

import com.jogamp.opengl.GL2;
import computergraphics.datastructures.mesh.ITriangleMesh;
import computergraphics.datastructures.mesh.Triangle;
import computergraphics.math.Matrix;
import computergraphics.math.Vector;
import computergraphics.rendering.RenderVertex;
import computergraphics.rendering.VertexBufferObject;

import java.util.ArrayList;
import java.util.List;

public class TriangleMeshNode extends LeafNode
{
    private ITriangleMesh triangleMesh;
    private boolean showNormals;
    private VertexBufferObject vbo;

    private Vector color = new Vector(0.75, 0.25, 0.25, 1);

    public TriangleMeshNode(ITriangleMesh triangleMesh)
    {
        this.triangleMesh = triangleMesh;
        this.vbo = new VertexBufferObject();
        createVbo();
    }

    private void createVbo()
    {
        List<RenderVertex> renderVertices = new ArrayList<>();

        for (int i = 0; i < triangleMesh.getNumberOfTriangles(); i++)
        {
            Triangle t = triangleMesh.getTriangle(i);

            Vector p0 = triangleMesh.getVertex(t.getVertexIndex(0)).getPosition();
            Vector p1 = triangleMesh.getVertex(t.getVertexIndex(1)).getPosition();
            Vector p2 = triangleMesh.getVertex(t.getVertexIndex(2)).getPosition();
            Vector normal = t.getNormal();


            addTriangleVertices(renderVertices, p0, p1, p2, normal, this.color); //texCoord0, texCoord1, texCoord2);

            if(showNormals) {
                // calculate center of the triangle
                Vector center = p0.add(p1).add(p2).multiply(1.0/3.0);
                // calculate endpoint of the line
                Vector c1 = center.add(normal.multiply(0.1));
                // calculate normale that is perpendicular to the line
                Vector newNormal = new Vector(normal.z(), normal.z(), -normal.x() - normal.y());
                // set third point of triangle next to the center
                Vector c2 = center.add(newNormal.multiply(0.001));
                // render line
                renderVertices.add(new RenderVertex(center, newNormal, color));
                renderVertices.add(new RenderVertex(c1, newNormal, color));
                renderVertices.add(new RenderVertex(c2, newNormal, color));
            }
        }
        vbo.Setup(renderVertices, GL2.GL_TRIANGLES);
    }


    @Override
    public void drawGL(GL2 gl, RenderMode mode, Matrix modelMatrix)
    {
        if (triangleMesh.getTexture() != null) {
            if (!triangleMesh.getTexture().isLoaded())
            {
                triangleMesh.getTexture().load(gl);
            }
        }

        if (mode == RenderMode.REGULAR) {
            // triangleMesh.getTexture().bind(gl);
            vbo.draw(gl);
        }
    }

    private void addTriangleVertices(List<RenderVertex> renderVertices, Vector p0,
        Vector p1, Vector p2, Vector normal, Vector color, Vector texCoord0, Vector texCoord1, Vector texCoord2)
    {
        renderVertices.add(new RenderVertex(p0, normal, color, texCoord0));
        renderVertices.add(new RenderVertex(p1, normal, color, texCoord1));
        renderVertices.add(new RenderVertex(p2, normal, color, texCoord2));
    }

    private void addTriangleVertices(List<RenderVertex> renderVertices, Vector p0,
        Vector p1, Vector p2, Vector normal, Vector color)
    {
        renderVertices.add(new RenderVertex(p0, normal, color));
        renderVertices.add(new RenderVertex(p1, normal, color));
        renderVertices.add(new RenderVertex(p2, normal, color));
    }
}
