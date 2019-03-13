package com.votlin.backend

import com.votlin.common.model.Speaker
import com.votlin.common.model.Talk
import com.votlin.common.model.Time
import com.votlin.common.model.Track
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toTalk(speakers: List<Speaker>): Talk = Talk(
        id = this[TalkVo.id],
        name = this[TalkVo.name],
        description = this[TalkVo.description],
        track = Track.valueOf(this[TalkVo.track]),
        time = Time(start = this[TalkVo.start], end = this[TalkVo.end]),
        speakers = speakers
)


fun ResultRow.toSpeaker(): Speaker = Speaker(
        name = this[SpeakerVo.name],
        bio = this[SpeakerVo.bio],
        twitter = this[SpeakerVo.twitter],
        linkedin = this[SpeakerVo.linkedin],
        photoUrl = this[SpeakerVo.photoUrl]
)