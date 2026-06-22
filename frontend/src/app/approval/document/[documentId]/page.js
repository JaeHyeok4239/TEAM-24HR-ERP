'use client'

import DocumentDetail from "@/components/approval/DocumentDetail";
import { use } from "react";

export default function DocumentDetailPage({ params }){
    const {documentId} = use(params);

    return (
        <div>
            <DocumentDetail documentId = {documentId}/>
        </div>
    )
}